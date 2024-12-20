package com.bruno13palhano.takeme.ui.screens.home.presenter

import androidx.compose.runtime.Immutable
import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.takeme.ui.shared.base.ViewAction
import com.bruno13palhano.takeme.ui.shared.base.ViewEvent
import com.bruno13palhano.takeme.ui.shared.base.ViewSideEffect
import com.bruno13palhano.takeme.ui.shared.base.ViewState

@Immutable
internal data class HomeState(
    val start: Boolean,
    val isSearch: Boolean,
    val isFieldInvalid: Boolean,
    val homeInputFields: HomeInputFields
) : ViewState {
    companion object {
        val initialState = HomeState(
            start = true,
            isSearch = false,
            isFieldInvalid = false,
            homeInputFields = HomeInputFields()
        )
    }
}

@Immutable
internal sealed interface HomeEvent : ViewEvent {
    data object Search : HomeEvent
    data class UpdateErrorResponse(val message: String?) : HomeEvent
    data class UpdateInternalError(val internalError: InternalError?) : HomeEvent
    data object NoDriverFound : HomeEvent
    data object DismissKeyboard : HomeEvent
    data object NavigateToDriverPicker : HomeEvent
}

@Immutable
internal sealed interface HomeSideEffect : ViewSideEffect {
    data class ShowResponseError(val message: String?) : HomeSideEffect
    data class ShowInternalError(val internalError: InternalError?) : HomeSideEffect
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