package com.bruno13palhano.takeme.ui.screens.travelhistory.viewmodel

import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryAction
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryActionProcessor
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryEvent
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryReducer
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistorySideEffect
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryState
import com.bruno13palhano.takeme.ui.shared.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class TravelHistoryViewModel @Inject constructor(

): BaseViewModel<TravelHistoryState, TravelHistoryAction, TravelHistoryEvent, TravelHistorySideEffect>(
    initialState = TravelHistoryState.initialState,
    actionProcessor = TravelHistoryActionProcessor(),
    reducer = TravelHistoryReducer()
) {
    override fun onAction(action: TravelHistoryAction) {
        return when (action) {
            is TravelHistoryAction.OnNavigateToHome -> sendEvent(TravelHistoryEvent.NavigateToHome)
        }
    }
}