package com.bruno13palhano.takeme

import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.data.model.Route
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.repository.FakeRideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeAction
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeReducer
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeSideEffect
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeState
import com.bruno13palhano.takeme.ui.screens.home.viewmodel.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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
internal class HomeViewModelTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val standardDispatcherRule = StandardDispatcherRule()

    private val customerId = "123456"
    private val origin = "Street A"
    private val destination = "Street B"

    private val validRideEstimate = RideEstimate(
        distance = 11111.0,
        duration = "1234s",
        drivers = emptyList(),
        route = Route.empty
    )

    private lateinit var repository: RideEstimateRepository
    private lateinit var sut: HomeViewModel

    @Before
    fun setUp() {
        repository = FakeRideEstimateRepository()
        sut = HomeViewModel(
            repository = repository,
            initialHomeState = HomeState.initialState,
            homeReducer = HomeReducer(),
            dispatcher = UnconfinedTestDispatcher(),
        )

        sut.state.value.homeInputFields.updateCustomerId(customerId)
        sut.state.value.homeInputFields.updateOrigin(origin)
        sut.state.value.homeInputFields.updateDestination(destination)
    }

    @Test
    fun checkHomeSideEffect_ShowErrorResponse() = runTest {
        val triggerFakeServerErrorResponse = "ServerErrorResponse"

        repository.insertRideEstimate(validRideEstimate)

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    HomeSideEffect.ShowResponseError("Unauthorized"),
                    effect
                )
            }
        }

        sut.state.value.homeInputFields.updateCustomerId(triggerFakeServerErrorResponse)

        sut.onAction(HomeAction.OnNavigateToDriverPicker)
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun checkHomeSideEffect_ShowInternalError() = runTest {
        val triggerFakeInternalError = "InternalError"

        repository.insertRideEstimate(validRideEstimate)

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    HomeSideEffect.ShowInternalError(InternalError.UNKNOWN_ERROR),
                    effect
                )
            }
        }

        sut.state.value.homeInputFields.updateCustomerId(triggerFakeInternalError)

        sut.onAction(HomeAction.OnNavigateToDriverPicker)
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun checkHomeSideEffect_ShowNoDriverFound() = runTest {
        repository.insertRideEstimate(RideEstimate.empty)

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    HomeSideEffect.ShowNoDriverFound,
                    effect
                )
            }
        }

        sut.onAction(HomeAction.OnNavigateToDriverPicker)
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun checkHomeSideEffect_InvalidFieldError() = runTest {
        repository.insertRideEstimate(validRideEstimate)

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    HomeSideEffect.InvalidFieldError,
                    effect
                )
            }
        }

        sut.state.value.homeInputFields.updateCustomerId("")

        sut.onAction(HomeAction.OnNavigateToDriverPicker)

        collectEffectJob.cancel()
    }

    @Test
    fun checkHomeSideEffect_DismissKeyboard() = runTest {
        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    HomeSideEffect.DismissKeyboard,
                    effect
                )
            }
        }

        sut.onAction(HomeAction.OnDismissKeyboard)
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun checkHomeSideEffect_NavigateToDriverPicker() = runTest {
        repository.insertRideEstimate(validRideEstimate)

        val collectEffectJob = launch {
            sut.sideEffect.collect { effect ->
                assertEquals(
                    HomeSideEffect.NavigateToDriverPicker(customerId, origin, destination),
                    effect
                )
            }
        }

        sut.onAction(HomeAction.OnNavigateToDriverPicker)
        advanceUntilIdle()

        collectEffectJob.cancel()
    }

    @Test
    fun when_HomeActionOnNavigateToDriverPicker_ifAllFieldsIsNotBlank_then_isSearch_equalsTrue() = runTest {
        repository.insertRideEstimate(validRideEstimate)

        sut.onAction(HomeAction.OnNavigateToDriverPicker)

        assertEquals(sut.state.value.isSearch, true)
    }

    @Test
    fun when_HomeActionOnNavigateToDriverPicker_ifAllFieldsIsNotBlank_then_isFieldInvalid_equalsFalse() = runTest {
        repository.insertRideEstimate(RideEstimate.empty)

        sut.onAction(HomeAction.OnNavigateToDriverPicker)

        assertEquals(sut.state.value.isFieldInvalid, false)
    }

    @Test
    fun when_HomeActionOnNavigateToDriverPicker_ifCustomerId_isBlank_then_isFieldInvalid_equalsTrue() = runTest {
        repository.insertRideEstimate(RideEstimate.empty)

        sut.state.value.homeInputFields.updateCustomerId("")

        sut.onAction(HomeAction.OnNavigateToDriverPicker)

        assertEquals(sut.state.value.isFieldInvalid, true)
    }

    @Test
    fun when_HomeActionOnNavigateToDriverPicker_ifOrigin_isBlank_then_isFieldInvalid_equalsTrue() = runTest {
        repository.insertRideEstimate(RideEstimate.empty)

        sut.state.value.homeInputFields.updateOrigin("")

        sut.onAction(HomeAction.OnNavigateToDriverPicker)

        assertEquals(sut.state.value.isFieldInvalid, true)
    }

    @Test
    fun when_HomeActionOnNavigateToDriverPicker_ifDestination_isBlank_then_isFieldInvalid_equalsTrue() = runTest {
        repository.insertRideEstimate(RideEstimate.empty)

        sut.state.value.homeInputFields.updateDestination("")

        sut.onAction(HomeAction.OnNavigateToDriverPicker)

        assertEquals(sut.state.value.isFieldInvalid, true)
    }
}