package com.bruno13palhano.takeme.ui.shared.base

internal interface Reducer<State: ViewState, Event: ViewEvent, SideEffect: ViewSideEffect> {
    fun reduce(previousState: State, event: Event): Pair<State, SideEffect?>
}