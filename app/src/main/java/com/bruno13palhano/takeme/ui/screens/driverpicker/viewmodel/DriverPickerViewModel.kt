package com.bruno13palhano.takeme.ui.screens.driverpicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno13palhano.data.di.ConfirmRideRep
import com.bruno13palhano.data.di.RideEstimateRep
import com.bruno13palhano.data.model.ConfirmRide
import com.bruno13palhano.data.model.RequestConfirmRide
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.repository.ConfirmRideRepository
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerEvent
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerSideEffect
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerState
import com.bruno13palhano.takeme.ui.shared.base.ContainerMVI
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DriverPickerViewModel @Inject constructor(
    @RideEstimateRep private val rideEstimateRepository: RideEstimateRepository,
    @ConfirmRideRep private val confirmRideRepository: ConfirmRideRepository
) : ViewModel() {
    val container = ContainerMVI<DriverPickerState, DriverPickerSideEffect>(
        initialState = DriverPickerState.initialState,
        scope = viewModelScope
    )

    fun sendEvent(event: DriverPickerEvent) {
        when (event) {
            is DriverPickerEvent.ChooseDriver -> chooseDriver(
                driverId = event.driverId,
                driverName = event.driverName,
                value = event.value
            )

            is DriverPickerEvent.NavigateBack -> navigateBack()

            is DriverPickerEvent.UpdateCustomerParams -> updateCustomerParams(
                customerId = event.customerId,
                origin = event.origin,
                destination = event.destination
            )

            is DriverPickerEvent.UpdateRideEstimate -> updateRideEstimate()
        }
    }

    private fun chooseDriver(driverId: Long, driverName: String, value: Float) = container.intent {
        reduce {
            copy(
                isLoading = true,
                driverId = driverId,
                driverName = driverName,
                value = value
            )
        }

        val response = confirmRideRepository.confirmRide(
            confirmRide = RequestConfirmRide(
                customerId = state.value.customerId,
                origin = state.value.origin,
                destination = state.value.destination,
                distance = state.value.rideEstimate.distance,
                duration = state.value.rideEstimate.duration,
                driverId = driverId,
                driverName = driverName,
                value = value
            )
        )

        processChooseDriverResponse(response = response)
    }

    private fun processChooseDriverResponse(response: Resource<ConfirmRide>) = container.intent {
        when (response) {
            is Resource.Success -> {
                response.data?.let {
                    if (it.success) {
                        sendSideEffect(DriverPickerSideEffect.NavigateToTravelHistory)
                    } else {
                        updateResponseError(message = response.internalError?.name)
                    }
                }
            }

            is Resource.ServerResponseError -> {
                updateResponseError(
                    message = response.remoteErrorResponse!!.errorDescription
                )
            }

            is Resource.Error -> {
                reduce { copy(isLoading = false) }
                sendSideEffect(
                    DriverPickerSideEffect.ShowInternalError(
                        internalError = response.internalError
                    )
                )
            }
        }
    }

    private fun navigateBack() = container.intent {
        reduce { copy(isLoading = false) }
        sendSideEffect(DriverPickerSideEffect.NavigateBack)
    }

    private fun updateCustomerParams(
        customerId: String,
        origin: String,
        destination: String
    ) = container.intent {
        reduce {
            copy(
                start = false,
                customerId = customerId,
                origin = origin,
                destination = destination
            )
        }
    }

    private fun updateRideEstimate() = container.intent {
        val rideEstimate = rideEstimateRepository.getLastRideEstimate()
        rideEstimate?.let {
            reduce { copy(rideEstimate = it) }
        }
    }

    private fun updateResponseError(message: String?) = container.intent {
        reduce { copy(isLoading = false) }
        sendSideEffect(DriverPickerSideEffect.ShowResponseError(message = message))
    }
}