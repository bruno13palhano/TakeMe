package com.bruno13palhano.takeme.ui.screens.home.presenter

import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.shared.base.ActionProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

internal class HomeActionProcessor(
    private val repository: RideEstimateRepository,
    private val scope: CoroutineScope
) : ActionProcessor<HomeAction, HomeState, HomeEvent> {
    override fun process(action: HomeAction, state: HomeState): Flow<HomeEvent> {
        return channelFlow {
            when (action) {
                is HomeAction.OnDismissKeyboard -> send(HomeEvent.DismissKeyboard)

                is HomeAction.OnNavigateToDriverPicker -> navigateToDriverPicker(state = state)
            }

            awaitClose()
        }
    }

    private suspend fun ProducerScope<HomeEvent>.navigateToDriverPicker(state: HomeState) {
        if (state.homeInputFields.isValid()) {
            send(HomeEvent.Search)

            scope.launch {
                val response = repository.searchDriver(
                    customerId = state.homeInputFields.customerId,
                    origin = state.homeInputFields.origin,
                    destination = state.homeInputFields.destination
                )

                processResponse(response = response)
            }
        } else {
            send(HomeEvent.InvalidFieldError)
        }
    }

    private suspend fun ProducerScope<HomeEvent>.processResponse(response: Resource<RideEstimate>) {
        when (response) {
            is Resource.Success -> successResponse(response = response)

            is Resource.ServerResponseError -> {
                send(
                    HomeEvent.UpdateErrorResponse(
                        message = response.remoteErrorResponse!!.errorDescription
                    )
                )
            }

            is Resource.Error -> {
                send(
                    HomeEvent.UpdateInternalError(internalError = response.internalError)
                )
            }
        }
    }

    private suspend fun ProducerScope<HomeEvent>.successResponse(response: Resource<RideEstimate>) {
        response.data?.let { rideEstimate ->
            if (rideEstimate.isNotEmpty()) {
                repository.insertRideEstimate(rideEstimate = rideEstimate)

                send(HomeEvent.NavigateToDriverPicker)
            } else {
                send(HomeEvent.NoDriverFound)
            }
        }
    }
}