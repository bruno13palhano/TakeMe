package com.bruno13palhano.takeme.ui.shared.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal abstract class BasePresenter<Action: ViewAction, Event: ViewEvent, State: ViewState, SideEffect: ViewSideEffect>(
    initialState: State,
    protected val actionProcessor: ActionProcessor<Action, State, Event>,
    protected val reducer: Reducer<State, Event, SideEffect>,
    protected val scope: CoroutineScope
) {
    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _sideEffects = Channel<SideEffect>(capacity = Channel.CONFLATED)
    val sideEffect = _sideEffects.receiveAsFlow()

    fun onAction(action: Action) {
        scope.launch {
            actionProcessor.process(action = action, state = state.value).collect { event ->
                val (newState, sideEffect) =
                    reducer.reduce(previousState = state.value, event = event)

                _state.value = newState
                sideEffect?.let { effect -> _sideEffects.trySend(effect) }
            }
        }
    }
}