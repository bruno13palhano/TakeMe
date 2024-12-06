package com.bruno13palhano.takeme.ui.shared.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

internal abstract class BaseViewModel<State: ViewState, Action: ViewAction, Event: ViewEvent, SideEffect: ViewSideEffect>(
    initialState: State,
    protected val actionProcessor: ActionProcessor<Action, Event>,
    protected val reducer: Reducer<State, Event, SideEffect>
) : ViewModel() {
    private var _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    val events: MutableSharedFlow<Event> = MutableSharedFlow(extraBufferCapacity = 20)

    private val _sideEffect = Channel<SideEffect>(capacity = Channel.CONFLATED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onAction(action: Action) {
        sendEvent(actionProcessor.process(action))
    }

    protected fun sendEvent(event: Event) {
        val (newState, sideEffect) = reducer.reduce(_state.value, event)

        _state.tryEmit(newState)

        sideEffect?.let {
            sendSideEffect(it)
        }
    }

    fun sendSideEffect(sideEffect: SideEffect) {
        _sideEffect.trySend(sideEffect)
    }
}