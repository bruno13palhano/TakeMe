package com.bruno13palhano.takeme.ui.screens.home.presenter

import com.bruno13palhano.takeme.ui.shared.base.Reducer

internal class HomeReducer : Reducer<HomeState, HomeEvent, HomeSideEffect> {
    override fun reduce(
        previousState: HomeState,
        event: HomeEvent
    ): Pair<HomeState, HomeSideEffect?> {
        return when (event) {
            is HomeEvent.Search -> search(previousState = previousState)

            is HomeEvent.Error -> {
                previousState.copy(
                    isSearch = false
                ) to HomeSideEffect.ShowError(message = event.message)
            }

            is HomeEvent.NoDriverFound -> {
                previousState.copy(isSearch = false) to HomeSideEffect.ShowNoDriverFound
            }

            is HomeEvent.DismissKeyboard -> previousState to HomeSideEffect.DismissKeyboard

            is HomeEvent.NavigateToDriverPicker -> navigateToDriverPicker(
                previousState = previousState
            )
        }
    }

    private fun search(previousState: HomeState): Pair<HomeState, HomeSideEffect?> {
        return if (previousState.homeInputFields.isValid()) {
            previousState.copy(
                isSearch = true,
                isFieldInvalid = false
            ) to null
        } else {
            previousState.copy(
                isSearch = false,
                isFieldInvalid = true
            ) to HomeSideEffect.InvalidFieldError
        }
    }

    private fun navigateToDriverPicker(
        previousState: HomeState
    ): Pair<HomeState, HomeSideEffect?> {
        val sideEffect = if (previousState.isFieldInvalid) {
            null
        } else {
            HomeSideEffect.NavigateToDriverPicker(
                customerId = previousState.homeInputFields.customerId,
                origin = previousState.homeInputFields.origin,
                destination = previousState.homeInputFields.destination
            )
        }

        return previousState to sideEffect
    }
}