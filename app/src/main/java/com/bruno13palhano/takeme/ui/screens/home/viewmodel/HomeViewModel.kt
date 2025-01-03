package com.bruno13palhano.takeme.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno13palhano.data.di.RideEstimateRep
import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeEvent
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeSideEffect
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeState
import com.bruno13palhano.takeme.ui.shared.base.ContainerMVI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    @RideEstimateRep private val repository: RideEstimateRepository
) : ViewModel() {
    val container = ContainerMVI<HomeState, HomeSideEffect>(
        initialState = HomeState.initialState,
        scope = viewModelScope
    )

    fun sendEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.DismissKeyboard -> dismissKeyboard()

            is HomeEvent.NavigateToDriverPicker -> navigateToDriverPicker()
        }
    }

    private fun dismissKeyboard() = container.intent {
        sendSideEffect(HomeSideEffect.DismissKeyboard)
    }

    private fun navigateToDriverPicker() = container.intent {
        if (state.value.homeInputFields.isValid()) {
            reduce { copy(isSearch = true, isFieldInvalid = false) }

            val response = repository.searchDriver(
                customerId = state.value.homeInputFields.customerId,
                origin = state.value.homeInputFields.origin,
                destination = state.value.homeInputFields.destination
            )

            processResponse(response = response)
        } else {
            invalidFieldError()
        }
    }

    private fun invalidFieldError() = container.intent {
        reduce { copy(isFieldInvalid = true) }
        sendSideEffect(HomeSideEffect.InvalidFieldError)
    }

    private fun processResponse(response: Resource<RideEstimate>) = container.intent {
        when (response) {
            is Resource.Success -> successResponse(response = response)

            is Resource.ServerResponseError -> {
                serverResponseError(message = response.remoteErrorResponse?.errorDescription)
            }

            is Resource.Error -> internalError(internalError = response.internalError)
        }
    }

    private fun serverResponseError(message: String?) = container.intent {
        reduce { copy(isSearch = false) }
        sendSideEffect(HomeSideEffect.ShowResponseError(message = message))
    }

    private fun internalError(internalError: InternalError?) = container.intent {
        reduce { copy(isSearch = false) }
        sendSideEffect(HomeSideEffect.ShowInternalError(internalError = internalError))
    }

    private fun successResponse(response: Resource<RideEstimate>) = container.intent {
        response.data?.let { rideEstimate ->
            if (rideEstimate.isNotEmpty()) {
                repository.insertRideEstimate(rideEstimate = rideEstimate)

                sendSideEffect(
                    HomeSideEffect.NavigateToDriverPicker(
                        customerId = state.value.homeInputFields.customerId,
                        origin = state.value.homeInputFields.origin,
                        destination = state.value.homeInputFields.destination
                    )
                )
            } else {
                noDriverFound()
            }
        }
    }

    private fun noDriverFound() = container.intent {
        reduce { copy(isSearch = false) }
        sendSideEffect(HomeSideEffect.ShowNoDriverFound)
    }
}