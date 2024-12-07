package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import com.bruno13palhano.takeme.ui.shared.base.Reducer

internal class TravelHistoryReducer :
    Reducer<TravelHistoryState, TravelHistoryEvent, TravelHistorySideEffect> {
    override fun reduce(
        previousState: TravelHistoryState,
        event: TravelHistoryEvent
    ): Pair<TravelHistoryState, TravelHistorySideEffect?> {
        return when (event) {
            is TravelHistoryEvent.Loading -> previousState.copy(isLoading = true) to null

            is TravelHistoryEvent.NavigateToHome -> {
                previousState.copy(isLoading = false) to TravelHistorySideEffect.NavigateToHome
            }
        }
    }
}