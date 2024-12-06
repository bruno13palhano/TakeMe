package com.bruno13palhano.takeme.ui.shared.base

internal interface ActionProcessor<Action: ViewAction, Event: ViewEvent> {
    fun process(action: Action): Event
}