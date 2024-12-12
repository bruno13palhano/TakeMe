package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

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
                previousState.copy(
                    isLoading = false,
                    drivers = event.drivers
                ) to null
            }

            is TravelHistoryEvent.UpdateRides -> {
                previousState.copy(
                    isLoading = false,
                    rides = event.rides
                ) to null
            }

            is TravelHistoryEvent.Error -> {
                previousState.copy(
                    isLoading = false
                ) to TravelHistorySideEffect.ShowError(message = event.message)
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