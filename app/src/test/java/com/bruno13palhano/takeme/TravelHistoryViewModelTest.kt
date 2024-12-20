package com.bruno13palhano.takeme

import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.data.model.Ride
import com.bruno13palhano.data.repository.DriverInfoRepository
import com.bruno13palhano.data.repository.RidesRepository
import com.bruno13palhano.takeme.repository.FakeDriverInfoRepository
import com.bruno13palhano.takeme.repository.FakeRidesRepository
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryAction
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryReducer
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistorySideEffect
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryState
import com.bruno13palhano.takeme.ui.screens.travelhistory.viewmodel.TravelHistoryViewModel
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
internal class TravelHistoryViewModelTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val standardDispatcherRule = StandardDispatcherRule()

    private lateinit var driverInfoRepository: DriverInfoRepository
    private lateinit var ridesRepository: RidesRepository
    private lateinit var sut: TravelHistoryViewModel

    @Before
    fun setup() {
        driverInfoRepository = FakeDriverInfoRepository()
        ridesRepository = FakeRidesRepository()
        sut = TravelHistoryViewModel(
            driverInfoRepository = driverInfoRepository,
            initialTravelHistoryState = TravelHistoryState.initialState,
            travelHistoryReducer = TravelHistoryReducer(),
            ridesRepository = ridesRepository,
            ioScope = TestScope(UnconfinedTestDispatcher())
        )
    }

    @Test
    fun when_ExpandedSelectorEvent_shouldUpdateExpandedSelector() = runTest {
        val expected = true

        sut.onAction(TravelHistoryAction.OnExpandSelector(expandSelector = expected))
        advanceUntilIdle()

        assertEquals(expected, sut.state.value.expandSelector)
    }

    @Test
    fun when_UpdateCurrentDriverEvent_shouldUpdateCurrentDriver() = runTest {
        val expected = DriverInfo(1, "Driver 1", 1.0f)

        sut.onAction(TravelHistoryAction.OnUpdateCurrentDriver(driver = expected))
        advanceUntilIdle()

        assertEquals(expected, sut.state.value.currentDriver)
    }

    @Test
    fun when_GetDriversEvent_shouldUpdateDrivers() = runTest {
        val expected = listOf(
            DriverInfo(1, "Driver 1", 1.0f),
            DriverInfo(2, "Driver 2", 2.0f)
        )

        expected.forEach { driverInfo ->
            driverInfoRepository.insertDriverInfo(driverInfo = driverInfo)
        }

        sut.onAction(TravelHistoryAction.OnGetDrivers)
        advanceUntilIdle()

        assertEquals(expected, sut.state.value.drivers)
    }

    @Test
    fun when_GetDriversEvent_shouldUpdateCurrentDriver_withFirstDriver() = runTest {
        val expected = DriverInfo(1, "Driver 1", 1.0f)
        val drivers = listOf(
            DriverInfo(1, "Driver 1", 1.0f),
            DriverInfo(2, "Driver 2", 2.0f)
        )

        drivers.forEach { driverInfo ->
            driverInfoRepository.insertDriverInfo(driverInfo = driverInfo)
        }

        sut.onAction(TravelHistoryAction.OnGetDrivers)
        advanceUntilIdle()

        assertEquals(expected, sut.state.value.currentDriver)
    }

    @Test
    fun when_GetCustomerRidesEvent_shouldUpdateRides_ifInputsFields_isValid_and_responseIsSuccessful() = runTest {
        val customerId = "123"
        val driverId = 1L
        val expected = listOf(
            Ride(
                id = 1L,
                date = "1",
                origin = "origin 1",
                destination = "destination 1",
                distance = 1000.0,
                duration = "1000s",
                driver = DriverInfo(1L, "Driver 1", 1.0f),
                value = 200f
            ),
            Ride(
                id = 4L,
                date = "11",
                origin = "origin 11",
                destination = "destination 11",
                distance = 1100.0,
                duration = "1100s",
                driver = DriverInfo(1L, "Driver 1", 1.0f),
                value = 230f
            )
        )

        sut.state.value.travelHistoryInputFields.updateCustomerId(customerId = customerId)

        sut.onAction(TravelHistoryAction.OnGetCustomerRides(customerId = customerId, driverId = driverId))
        advanceUntilIdle()

        assertEquals(expected, sut.state.value.rides)
    }

    @Test
    fun when_GetCustomerRidesEvent_shouldUpdateRides_ifInputsFields_isValid_and_responseIsServerError() = runTest {
        val customerId = "ServerErrorResponse"
        val driverId = 1L

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    TravelHistorySideEffect.ShowResponseError(message = "Unauthorized"),
                    effect
                )
            }
        }

        sut.state.value.travelHistoryInputFields.updateCustomerId(customerId = customerId)

        sut.onAction(TravelHistoryAction.OnGetCustomerRides(customerId = customerId, driverId = driverId))
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun when_GetCustomerRidesEvent_shouldUpdateRides_ifInputsFields_isValid_and_responseIsInternalError() = runTest {
        val customerId = "InternalError"
        val driverId = 1L

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    TravelHistorySideEffect.ShowInternalError(
                        internalError = InternalError.UNKNOWN_ERROR
                    ),
                    effect
                )
            }
        }

        sut.state.value.travelHistoryInputFields.updateCustomerId(customerId = customerId)

        sut.onAction(TravelHistoryAction.OnGetCustomerRides(customerId = customerId, driverId = driverId))
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun when_GetCustomerRidesEvent_and_InputsFields_isInvalid_then_emit_InvalidFieldErrorSideEffect() = runTest {
        val customerId = ""

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    TravelHistorySideEffect.InvalidFieldError,
                    effect
                )
            }
        }

        sut.state.value.travelHistoryInputFields.updateCustomerId(customerId = customerId)

        sut.onAction(TravelHistoryAction.OnGetCustomerRides(customerId = customerId, driverId = 1L))
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun when_DismissKeyboardAction_then_emit_DismissKeyboardSideEffect() = runTest {
        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    TravelHistorySideEffect.DismissKeyboard,
                    effect
                )
            }
        }

        sut.onAction(TravelHistoryAction.OnDismissKeyboard)
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun when_OnNavigateToHome_then_emit_NavigateToHomeSideEffect() = runTest {
        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    TravelHistorySideEffect.NavigateToHome,
                    effect
                )
            }
        }

        sut.onAction(TravelHistoryAction.OnNavigateToHome)
        advanceUntilIdle()

        collectEffectJob.cancel()
    }
}