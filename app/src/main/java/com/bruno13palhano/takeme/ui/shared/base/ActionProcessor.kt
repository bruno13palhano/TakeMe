package com.bruno13palhano.takeme.ui.shared.base

import kotlinx.coroutines.flow.Flow

internal interface ActionProcessor<Action: ViewAction, State: ViewState, Event: ViewEvent> {
    fun process(action: Action, state: State): Flow<Event>
}