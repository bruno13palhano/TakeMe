package com.bruno13palhano.takeme.ui.shared.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

internal class ContainerMVI<STATE, EFFECT>(
    initialState: STATE,
    private val scope: CoroutineScope
) {
    private val _state: MutableStateFlow<STATE> = MutableStateFlow(initialState)
    val state: StateFlow<STATE> = _state.asStateFlow()

    private val _sideEffects = Channel<EFFECT>(capacity = Channel.CONFLATED)
    val sideEffect = _sideEffects.receiveAsFlow()

    fun intent(transform: suspend ContainerMVI<STATE, EFFECT>.() -> Unit) {
        scope.launch(SINGLE_THREAD) {
            this@ContainerMVI.transform()
        }
    }

    suspend fun reduce(reducer: suspend STATE.() -> STATE) {
        withContext(SINGLE_THREAD) {
            _state.value = _state.value.reducer()
        }
    }

    suspend fun sendSideEffect(sideEffect: EFFECT) {
        _sideEffects.send(sideEffect)
    }

    companion object {
        @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
        private val SINGLE_THREAD = newSingleThreadContext("mvi")
    }
}