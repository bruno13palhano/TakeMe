package com.bruno13palhano.takeme.ui.screens.home.presenter

import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.shared.base.ActionProcessor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

internal class HomeActionProcessor(
    private val repository: RideEstimateRepository
) : ActionProcessor<HomeAction, HomeState, HomeEvent> {
    override fun process(action: HomeAction, state: HomeState): Flow<HomeEvent> {
        return flow {
            when (action) {
                is HomeAction.OnDismissKeyboard -> emit(HomeEvent.DismissKeyboard)

                is HomeAction.OnNavigateToDriverPicker -> navigateToDriverPicker(state = state)
            }
        }
    }

    private suspend fun FlowCollector<HomeEvent>.navigateToDriverPicker(state: HomeState) {
        if (state.homeInputFields.isValid()) {
            emit(HomeEvent.Search)

            val response = repository.searchDriver(
                customerId = state.homeInputFields.customerId,
                origin = state.homeInputFields.origin,
                destination = state.homeInputFields.destination
            )

            processResponse(response = response)
        } else {
            emit(HomeEvent.InvalidFieldError)
        }
    }

    private suspend fun FlowCollector<HomeEvent>.processResponse(response: Resource<RideEstimate>) {
        when (response) {
            is Resource.Success -> successResponse(response = response)

            is Resource.ServerResponseError -> {
                emit(
                    HomeEvent.UpdateErrorResponse(
                        message = response.remoteErrorResponse!!.errorDescription
                    )
                )
            }

            is Resource.Error -> {
                emit(
                    HomeEvent.UpdateInternalError(internalError = response.internalError)
                )
            }
        }
    }

    private suspend fun FlowCollector<HomeEvent>.successResponse(response: Resource<RideEstimate>) {
        response.data?.let { rideEstimate ->
            if (rideEstimate.isNotEmpty()) {
                repository.insertRideEstimate(rideEstimate = rideEstimate)

                emit(HomeEvent.NavigateToDriverPicker)
            } else {
                emit(HomeEvent.NoDriverFound)
            }
        }
    }
}