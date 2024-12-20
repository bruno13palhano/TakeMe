package com.bruno13palhano.takeme.ui.shared.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal abstract class BaseViewModel<State: ViewState, Action: ViewAction, Event: ViewEvent, SideEffect: ViewSideEffect>(
    initialState: State,
    protected val actionProcessor: ActionProcessor<Action, State, Event>,
    protected val reducer: Reducer<State, Event, SideEffect>
) : ViewModel() {
    private var _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _sideEffect = Channel<SideEffect>(capacity = Channel.CONFLATED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onAction(action: Action) {
        viewModelScope.launch {
            actionProcessor.process(action, _state.value).collect { event ->
                val (newState, sideEffect) = reducer.reduce(_state.value, event)

                _state.value = newState

                sideEffect?.let {
                    _sideEffect.trySend(sideEffect)
                }
            }
        }
    }
}