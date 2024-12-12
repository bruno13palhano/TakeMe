package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.takeme.ui.shared.base.Reducer

internal class TravelHistoryReducer :
    Reducer<TravelHistoryState, TravelHistoryEvent, TravelHistorySideEffect> {
    override fun reduce(
        previousState: TravelHistoryState,
        event: TravelHistoryEvent
    ): Pair<TravelHistoryState, TravelHistorySideEffect?> {
        return when (event) {
            is TravelHistoryEvent.ExpandSelector -> {
                previousState.copy(expandSelector = event.expandSelector) to null
            }

            is TravelHistoryEvent.UpdateCurrentDriver -> {
                previousState.copy(currentDriver = event.driver) to null
            }

            is TravelHistoryEvent.GetDrivers -> {
                val currentDriver = event.drivers.firstOrNull() ?: DriverInfo.empty

                previousState.copy(
                    start = false,
                    isLoading = false,
                    currentDriver = currentDriver,
                    drivers = event.drivers
                ) to null
            }

            is TravelHistoryEvent.UpdateRides -> {
                previousState.copy(
                    isLoading = false,
                    rides = event.rides
                ) to null
            }

            is TravelHistoryEvent.UpdateResponseError -> {
                previousState.copy(
                    isLoading = false,
                    rides = emptyList()
                ) to TravelHistorySideEffect.ShowResponseError(message = event.message)
            }

            is TravelHistoryEvent.UpdateInternalError -> {
                previousState.copy(
                    isLoading = false,
                    rides = emptyList()
                ) to TravelHistorySideEffect.ShowInternalError(internalError = event.internalError)
            }

            is TravelHistoryEvent.InvalidFieldError -> {
                previousState.copy(
                    isLoading = false,
                    isFieldInvalid = true
                ) to TravelHistorySideEffect.InvalidFieldError
            }

            is TravelHistoryEvent.GetCustomerRides -> {
                previousState.copy(
                    isLoading = true,
                    isFieldInvalid = false,
                    customerId = event.customerId,
                    driverId = event.driverId
                ) to null
            }

            is TravelHistoryEvent.DismissKeyboard -> {
                previousState to TravelHistorySideEffect.DismissKeyboard
            }

            is TravelHistoryEvent.NavigateToHome -> {
                previousState.copy(isLoading = false) to TravelHistorySideEffect.NavigateToHome
            }
        }
    }
}