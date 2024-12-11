package com.bruno13palhano.takeme.ui.screens.home.presenter

import androidx.compose.runtime.Immutable
import com.bruno13palhano.takeme.ui.shared.base.ViewAction
import com.bruno13palhano.takeme.ui.shared.base.ViewEvent
import com.bruno13palhano.takeme.ui.shared.base.ViewSideEffect
import com.bruno13palhano.takeme.ui.shared.base.ViewState

@Immutable
internal data class HomeState(
    val isSearch: Boolean,
    val isFieldInvalid: Boolean,
    val homeInputFields: HomeInputFields
) : ViewState {
    companion object {
        val initialState = HomeState(
            isSearch = false,
            isFieldInvalid = false,
            homeInputFields = HomeInputFields()
        )
    }
}

@Immutable
internal sealed interface HomeEvent : ViewEvent {
    data object Search : HomeEvent
    data class Error(val message: String?) : HomeEvent
    data object NoDriverFound : HomeEvent
    data object DismissKeyboard : HomeEvent
    data object NavigateToDriverPicker : HomeEvent
}

@Immutable
internal sealed interface HomeSideEffect : ViewSideEffect {
    data class ShowError(val message: String?) : HomeSideEffect
    data object ShowNoDriverFound : HomeSideEffect
    data object InvalidFieldError : HomeSideEffect
    data object DismissKeyboard : HomeSideEffect
    data class NavigateToDriverPicker(
        val customerId: String,
        val origin: String,
        val destination: String
    ) : HomeSideEffect
}

@Immutable
internal sealed interface HomeAction : ViewAction {
    data object OnDismissKeyboard : HomeAction
    data object OnNavigateToDriverPicker: HomeAction
}