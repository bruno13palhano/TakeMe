package com.bruno13palhano.takeme.ui.screens.home.presenter

import com.bruno13palhano.takeme.ui.shared.base.Reducer

internal class HomeReducer : Reducer<HomeState, HomeEvent, HomeSideEffect> {
    override fun reduce(
        previousState: HomeState,
        event: HomeEvent
    ): Pair<HomeState, HomeSideEffect?> {
        return when (event) {
            is HomeEvent.Loading -> previousState.copy(isLoading = true) to null

            is HomeEvent.NavigateToDriverPicker -> {
                navigateToDriverPicker(previousState = previousState)
            }
        }
    }

    private fun navigateToDriverPicker(
        previousState: HomeState
    ): Pair<HomeState, HomeSideEffect?> {
        return if (previousState.homeInputFields.isValid()) {
            previousState.copy(
                isLoading = false,
                isFieldInvalid = false
            ) to HomeSideEffect.NavigateToDriverPicker
        } else {
            previousState.copy(
                isLoading = false,
                isFieldInvalid = true
            ) to HomeSideEffect.InvalidFieldError
        }
    }
}