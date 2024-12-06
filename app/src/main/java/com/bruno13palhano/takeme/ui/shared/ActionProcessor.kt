package com.bruno13palhano.takeme.ui.shared

internal interface ActionProcessor<Action: ViewAction, Event: ViewEvent> {
    fun process(action: Action): Event
}