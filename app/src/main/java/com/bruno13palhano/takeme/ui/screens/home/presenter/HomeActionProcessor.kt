package com.bruno13palhano.takeme.ui.screens.home.presenter

import com.bruno13palhano.takeme.ui.shared.base.ActionProcessor

internal class HomeActionProcessor : ActionProcessor<HomeAction, HomeEvent> {
    override fun process(action: HomeAction): HomeEvent {
        return when (action) {
            is HomeAction.NavigateToDriverPicker -> HomeEvent.NavigateToDriverPicker
        }
    }
}