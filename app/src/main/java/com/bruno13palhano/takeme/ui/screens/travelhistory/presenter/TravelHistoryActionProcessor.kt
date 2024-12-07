package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import com.bruno13palhano.takeme.ui.shared.base.ActionProcessor

internal class TravelHistoryActionProcessor : ActionProcessor<TravelHistoryAction, TravelHistoryEvent> {
    override fun process(action: TravelHistoryAction): TravelHistoryEvent {
        return when (action) {
            is TravelHistoryAction.OnNavigateToHome -> TravelHistoryEvent.NavigateToHome
        }
    }
}