package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import com.bruno13palhano.takeme.ui.shared.base.Reducer

internal class DriverPickerReducer :
    Reducer<DriverPickerState, DriverPickerEvent, DriverPickerSideEffect> {
    override fun reduce(
        previousState: DriverPickerState,
        event: DriverPickerEvent
    ): Pair<DriverPickerState, DriverPickerSideEffect?> {
        return when (event) {
            is DriverPickerEvent.Loading -> previousState.copy(isLoading = true) to null

            is DriverPickerEvent.NavigateToTravelHistory -> {
                previousState.copy(isLoading = false) to DriverPickerSideEffect.NavigateToTravelHistory
            }

            is DriverPickerEvent.NavigateBack -> {
                previousState.copy(isLoading = false) to DriverPickerSideEffect.NavigateBack
            }
        }
    }
}