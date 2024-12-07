package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import androidx.compose.runtime.Immutable
import com.bruno13palhano.takeme.ui.shared.base.ViewAction
import com.bruno13palhano.takeme.ui.shared.base.ViewEvent
import com.bruno13palhano.takeme.ui.shared.base.ViewSideEffect
import com.bruno13palhano.takeme.ui.shared.base.ViewState

@Immutable
internal data class DriverPickerState(
    val isLoading: Boolean
) : ViewState {
    companion object {
        val initialState = DriverPickerState(
            isLoading = false
        )
    }
}

@Immutable
internal sealed interface DriverPickerEvent : ViewEvent {
    data object Loading : DriverPickerEvent
    data object NavigateToTravelHistory : DriverPickerEvent
    data object NavigateBack : DriverPickerEvent
}

@Immutable
internal sealed interface DriverPickerSideEffect : ViewSideEffect {
    data object NavigateToTravelHistory : DriverPickerSideEffect
    data object NavigateBack : DriverPickerSideEffect
}

@Immutable
internal sealed interface DriverPickerAction : ViewAction {
    data object OnNavigateToTravelHistory : DriverPickerAction
    data object OnNavigateBack : DriverPickerAction
}