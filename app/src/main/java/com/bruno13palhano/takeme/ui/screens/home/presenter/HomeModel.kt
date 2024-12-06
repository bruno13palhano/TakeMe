package com.bruno13palhano.takeme.ui.screens.home.presenter

import androidx.compose.runtime.Immutable
import com.bruno13palhano.takeme.ui.shared.base.ViewAction
import com.bruno13palhano.takeme.ui.shared.base.ViewEvent
import com.bruno13palhano.takeme.ui.shared.base.ViewSideEffect
import com.bruno13palhano.takeme.ui.shared.base.ViewState

@Immutable
internal data class HomeState(
    val isLoading: Boolean,
    val isFieldInvalid: Boolean,
    val homeInputFields: HomeInputFields
) : ViewState {
    companion object {
        val initialState = HomeState(
            isLoading = false,
            isFieldInvalid = false,
            homeInputFields = HomeInputFields()
        )
    }
}

@Immutable
internal sealed interface HomeEvent : ViewEvent {
    data object Loading : HomeEvent
    data object NavigateToDriverPicker : HomeEvent
}

@Immutable
internal sealed interface HomeSideEffect : ViewSideEffect {
    data object InvalidFieldError : HomeSideEffect
    data object NavigateToDriverPicker : HomeSideEffect
}

@Immutable
internal sealed interface HomeAction : ViewAction {
    data object NavigateToDriverPicker : HomeAction
}