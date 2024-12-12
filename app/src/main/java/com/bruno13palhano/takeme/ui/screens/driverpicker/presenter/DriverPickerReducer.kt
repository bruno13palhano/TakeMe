package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import com.bruno13palhano.takeme.ui.shared.base.Reducer

internal class DriverPickerReducer :
    Reducer<DriverPickerState, DriverPickerEvent, DriverPickerSideEffect> {
    override fun reduce(
        previousState: DriverPickerState,
        event: DriverPickerEvent
    ): Pair<DriverPickerState, DriverPickerSideEffect?> {
        return when (event) {
            is DriverPickerEvent.UpdateCustomerParams -> {
                previousState.copy(
                    start = false,
                    customerId = event.customerId,
                    origin = event.origin,
                    destination = event.destination
                ) to null
            }

            is DriverPickerEvent.UpdateResponseError -> {
                previousState.copy(
                    isLoading = false
                ) to DriverPickerSideEffect.ShowResponseError(message = event.message)
            }

            is DriverPickerEvent.UpdateInternalError -> {
                previousState.copy(
                    isLoading = false
                ) to DriverPickerSideEffect.ShowInternalError(internalError = event.internalError)
            }

            is DriverPickerEvent.UpdateRideEstimate -> {
                previousState.copy(rideEstimate = event.rideEstimate) to null
            }

            is DriverPickerEvent.ChooseDriver -> {
                previousState.copy(
                    isLoading = true,
                    driverId = event.driverId,
                    driverName = event.driverName,
                    value = event.value
                ) to null
            }

            is DriverPickerEvent.NavigateToTravelHistory -> {
                previousState to DriverPickerSideEffect.NavigateToTravelHistory
            }

            is DriverPickerEvent.NavigateBack -> {
                previousState.copy(isLoading = false) to DriverPickerSideEffect.NavigateBack
            }
        }
    }
}