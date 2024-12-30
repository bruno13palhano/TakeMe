package com.bruno13palhano.takeme.ui.shared.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal abstract class BasePresenter<Action: ViewAction, Event: ViewEvent, State: ViewState, SideEffect: ViewSideEffect>(
    initialState: State,
    protected val actionProcessor: ActionProcessor<Action, State, Event>,
    protected val reducer: Reducer<State, Event, SideEffect>
) {
    private val scope = CloseableCoroutineScope(coroutineContext = SupervisorJob() + Dispatchers.Main.immediate)

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

internal class CloseableCoroutineScope(
    override val coroutineContext: CoroutineContext
) : AutoCloseable, CoroutineScope {

    override fun close() = coroutineContext.cancel()
}