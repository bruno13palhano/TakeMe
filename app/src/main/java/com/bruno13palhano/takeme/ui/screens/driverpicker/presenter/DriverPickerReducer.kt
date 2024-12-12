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
                    customerId = event.customerId,
                    origin = event.origin,
                    destination = event.destination
                ) to null
            }

            is DriverPickerEvent.Error -> {
                previousState.copy(
                    isLoading = false
                ) to DriverPickerSideEffect.ShowError(message = event.message)
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