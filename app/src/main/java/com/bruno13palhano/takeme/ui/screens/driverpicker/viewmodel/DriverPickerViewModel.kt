package com.bruno13palhano.takeme.ui.screens.driverpicker.viewmodel

import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerAction
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerActionProcessor
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerEvent
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerReducer
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerSideEffect
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerState
import com.bruno13palhano.takeme.ui.shared.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DriverPickerViewModel @Inject constructor(

) : BaseViewModel<DriverPickerState, DriverPickerAction, DriverPickerEvent, DriverPickerSideEffect>(
    initialState = DriverPickerState.initialState,
    actionProcessor = DriverPickerActionProcessor(),
    reducer = DriverPickerReducer()
) {
    override fun onAction(action: DriverPickerAction) {
        return when (action) {
            is DriverPickerAction.OnNavigateToTravelHistory -> sendEvent(DriverPickerEvent.NavigateToTravelHistory)

            is DriverPickerAction.OnNavigateBack -> sendEvent(DriverPickerEvent.NavigateBack)
        }
    }
}