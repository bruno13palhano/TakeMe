package com.bruno13palhano.takeme

import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.data.model.Route
import com.bruno13palhano.data.repository.ConfirmRideRepository
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.repository.FakeConfirmRideRepository
import com.bruno13palhano.takeme.repository.FakeRideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerAction
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerReducer
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerSideEffect
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerState
import com.bruno13palhano.takeme.ui.screens.driverpicker.viewmodel.DriverPickerViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
internal class DriverPickerViewModelTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val standardDispatcherRule = StandardDispatcherRule()

    private lateinit var rideEstimateRepository: RideEstimateRepository
    private lateinit var confirmRideRepository: ConfirmRideRepository
    private lateinit var sut: DriverPickerViewModel

    @Before
    fun setup() {
        rideEstimateRepository = FakeRideEstimateRepository()
        confirmRideRepository = FakeConfirmRideRepository()
        sut = DriverPickerViewModel(
            rideEstimateRepository = rideEstimateRepository,
            confirmRideRepository = confirmRideRepository,
            defaultDriverPickerState = DriverPickerState.initialState,
            defaultDriverPickerReducer = DriverPickerReducer(),
            ioScope = TestScope(UnconfinedTestDispatcher())
        )
    }

    @Test
    fun when_OnGetLastRideEstimate_the_state_should_be_updated() = runTest {
        val rideEstimate = RideEstimate(
            distance = 2500.0,
            duration = "1234s",
            drivers = emptyList(),
            route = Route.empty
        )

        rideEstimateRepository.insertRideEstimate(rideEstimate = rideEstimate)
        advanceUntilIdle()

        sut.onAction(DriverPickerAction.OnGetLastRideEstimate)
        advanceUntilIdle()

        assertEquals(rideEstimate, sut.state.value.rideEstimate)
    }

    @Test
    fun when_OnUpdateCustomerParamsAction_the_customerId_origin_and_destination_should_be_updated() = runTest {
        sut.onAction(
            action = DriverPickerAction.OnUpdateCustomerParams(
                customerId = "1",
                origin = "origin",
                destination = "destination"
            )
        )

        advanceUntilIdle()

        assertEquals("1", sut.state.value.customerId)
        assertEquals("origin", sut.state.value.origin)
        assertEquals("destination", sut.state.value.destination)
    }

    @Test
    fun when_OnChooseDriverAction_ifSuccess_return_False_the_sideEffect_should_emit_ShowResponseError() = runTest {
        val fakeError = "No driver found"
        val triggerInternalError = DriverInfo(0L, "NoDriverFound", 0f)

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    DriverPickerSideEffect.ShowResponseError(
                        message = fakeError,
                    ),
                    effect
                )
            }
        }

        sut.onAction(
            action = DriverPickerAction.OnChooseDriver(
                driverId = triggerInternalError.id,
                driverName = triggerInternalError.name,
                value = triggerInternalError.minKm!!
            )
        )
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun when_OnChooseDriverAction_ifMinKm_is_less_than_distance_the_sideEffect_should_emit_ShowResponseError() = runTest {
        val fakeError = "Invalid distance"
        val triggerInternalError = DriverInfo(1L, "Driver 1", 0.5f)

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    DriverPickerSideEffect.ShowResponseError(
                        message = fakeError,
                    ),
                    effect
                )
            }
        }

        sut.onAction(
            action = DriverPickerAction.OnChooseDriver(
                driverId = triggerInternalError.id,
                driverName = triggerInternalError.name,
                value = triggerInternalError.minKm!!
            )
        )
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun when_OnChooseDriverAction_fail_the_sideEffect_should_emit_ShowInternalError() = runTest {
        val internalError = InternalError.UNKNOWN_ERROR
        val triggerInternalError = DriverInfo(0L, "InternalError", 0f)

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    DriverPickerSideEffect.ShowInternalError(internalError),
                    effect
                )
            }
        }

        sut.onAction(
            action = DriverPickerAction.OnChooseDriver(
                driverId = triggerInternalError.id,
                driverName = triggerInternalError.name,
                value = triggerInternalError.minKm!!
            )
        )
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun when_OnNavigateToTravelHistory_the_sideEffect_should_emit_NavigateToTravelHistory() = runTest {
        val rideEstimate = RideEstimate(
            distance = 2500.0,
            duration = "1234s",
            drivers = emptyList(),
            route = Route.empty
        )

        rideEstimateRepository.insertRideEstimate(rideEstimate = rideEstimate)
        sut.onAction(DriverPickerAction.OnGetLastRideEstimate)
        advanceUntilIdle()

        val triggerInternalError = DriverInfo(1L, "Driver 1", 2.5f)

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    DriverPickerSideEffect.NavigateToTravelHistory,
                    effect
                )
            }
        }

        sut.onAction(
            action = DriverPickerAction.OnChooseDriver(
                driverId = triggerInternalError.id,
                driverName = triggerInternalError.name,
                value = triggerInternalError.minKm!!
            )
        )
        sut.onAction(DriverPickerAction.OnNavigateToTravelHistory)
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun when_NavigateBackAction_the_sideEffect_should_emit_NavigateBack() = runTest {
        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    DriverPickerSideEffect.NavigateBack,
                    effect
                )
            }
        }

        sut.onAction(DriverPickerAction.OnNavigateBack)
        advanceUntilIdle()

        collectEffectJob.cancel()
    }
}