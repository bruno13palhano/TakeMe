package com.bruno13palhano.takeme.ui.screens.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.bruno13palhano.data.di.Dispatcher
import com.bruno13palhano.data.di.RideEstimateRep
import com.bruno13palhano.data.di.TakeMeDispatchers
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.di.DefaultHomeReducer
import com.bruno13palhano.takeme.ui.screens.di.DefaultHomeState
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeAction
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeEvent
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeReducer
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeSideEffect
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeState
import com.bruno13palhano.takeme.ui.shared.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    @RideEstimateRep private val repository: RideEstimateRepository,
    @DefaultHomeState private val initialHomeState: HomeState,
    @DefaultHomeReducer private val homeReducer: HomeReducer,
    @Dispatcher(TakeMeDispatchers.IO) private val dispatcher: CoroutineDispatcher
) : BaseViewModel<HomeState, HomeAction, HomeEvent, HomeSideEffect>(
    initialState = initialHomeState,
    reducer = homeReducer
) {
    override fun onAction(action: HomeAction) {
        when (action) {
            is HomeAction.OnDismissKeyboard -> sendEvent(HomeEvent.DismissKeyboard)

            is HomeAction.OnNavigateToDriverPicker -> navigateToDriverPicker()
        }
    }

    private fun navigateToDriverPicker() {
        if (state.value.homeInputFields.isValid()) {
            sendEvent(HomeEvent.Search)

            viewModelScope.launch(SupervisorJob() + dispatcher) {
                val response = repository.searchDriver(
                    customerId = state.value.homeInputFields.customerId,
                    origin = state.value.homeInputFields.origin,
                    destination = state.value.homeInputFields.destination
                )

                processResponse(response = response)
            }
        } else {
            sendEvent(HomeEvent.InvalidFieldError)
        }
    }

    private suspend fun processResponse(response: Resource<RideEstimate>) {
        when (response) {
            is Resource.Success -> successResponse(response = response)

            is Resource.ServerResponseError -> {
                sendEvent(
                    event = HomeEvent.UpdateErrorResponse(
                        message = response.remoteErrorResponse!!.errorDescription
                    )
                )
            }

            is Resource.Error -> {
                sendEvent(
                    event = HomeEvent.UpdateInternalError(internalError = response.internalError)
                )
            }
        }
    }

    private suspend fun successResponse(response: Resource<RideEstimate>) {
        response.data?.let { rideEstimate ->
            if (rideEstimate.isNotEmpty()) {
                repository.insertRideEstimate(rideEstimate = rideEstimate)

                sendEvent(event = HomeEvent.NavigateToDriverPicker)
            } else {
                sendEvent(event = HomeEvent.NoDriverFound)
            }
        }
    }
}