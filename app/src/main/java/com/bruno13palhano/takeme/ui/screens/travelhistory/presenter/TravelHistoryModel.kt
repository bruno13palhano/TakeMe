package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import androidx.compose.runtime.Immutable
import com.bruno13palhano.takeme.ui.shared.base.ViewAction
import com.bruno13palhano.takeme.ui.shared.base.ViewEvent
import com.bruno13palhano.takeme.ui.shared.base.ViewSideEffect
import com.bruno13palhano.takeme.ui.shared.base.ViewState

@Immutable
internal data class TravelHistoryState(
    val isLoading: Boolean
) : ViewState {
    companion object {
        val initialState = TravelHistoryState(
            isLoading = false
        )
    }
}

@Immutable
internal sealed interface TravelHistoryEvent : ViewEvent {
    data object Loading : TravelHistoryEvent
    data object NavigateToHome : TravelHistoryEvent
}

@Immutable
internal sealed interface TravelHistorySideEffect : ViewSideEffect {
    data object NavigateToHome : TravelHistorySideEffect
}

@Immutable
internal sealed interface TravelHistoryAction : ViewAction {
    data object OnNavigateToHome : TravelHistoryAction
}