package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import com.bruno13palhano.takeme.ui.shared.base.ActionProcessor

internal class DriverPickerActionProcessor :
    ActionProcessor<DriverPickerAction, DriverPickerEvent> {
    override fun process(action: DriverPickerAction): DriverPickerEvent {
        return when (action) {
            is DriverPickerAction.OnNavigateToTravelHistory -> DriverPickerEvent.NavigateToTravelHistory

            is DriverPickerAction.OnNavigateBack -> DriverPickerEvent.NavigateBack
        }
    }
}