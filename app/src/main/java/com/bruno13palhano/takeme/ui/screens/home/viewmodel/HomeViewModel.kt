package com.bruno13palhano.takeme.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno13palhano.data.di.RideEstimateRep
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeEvent
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeSideEffect
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    @RideEstimateRep private val repository: RideEstimateRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState.initialState)
    val state = _state

    private val events = MutableSharedFlow<HomeEvent>(extraBufferCapacity = 20)

    private val _sideEffect = Channel<HomeSideEffect>(capacity = Channel.CONFLATED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun sendEvent(event: HomeEvent) {
        if (!events.tryEmit(event)) {
            throw IllegalStateException("Event buffer overflow")
        }
    }

    fun handleEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
                    is HomeEvent.DismissKeyboard -> {
                        _sideEffect.trySend(HomeSideEffect.DismissKeyboard)
                    }

                    is HomeEvent.NavigateToDriverPicker -> navigateToDriverPicker()
                }
            }
        }
    }

    private suspend fun navigateToDriverPicker() {
        if (_state.value.homeInputFields.isValid()) {
            _state.value = _state.value.copy(isSearch = true, isFieldInvalid = false)

            val response = repository.searchDriver(
                customerId = _state.value.homeInputFields.customerId,
                origin = _state.value.homeInputFields.origin,
                destination = _state.value.homeInputFields.destination
            )

            processResponse(response = response)
        } else {
            _state.value = _state.value.copy(isSearch = false, isFieldInvalid = true)
            _sideEffect.trySend(HomeSideEffect.InvalidFieldError)
        }
    }

    private suspend fun processResponse(response: Resource<RideEstimate>) {
        when (response) {
            is Resource.Success -> successResponse(response = response)

            is Resource.ServerResponseError -> {
                _state.value = _state.value.copy(isSearch = false)
                _sideEffect.trySend(
                    HomeSideEffect.ShowResponseError(
                        message = response.remoteErrorResponse?.errorDescription
                    )
                )
            }

            is Resource.Error -> {
                _state.value = _state.value.copy(isSearch = false)
                _sideEffect.trySend(
                    HomeSideEffect.ShowInternalError(internalError = response.internalError)
                )
            }
        }
    }

    private suspend fun successResponse(response: Resource<RideEstimate>) {
        response.data?.let { rideEstimate ->
            if (rideEstimate.isNotEmpty()) {
                repository.insertRideEstimate(rideEstimate = rideEstimate)

                _sideEffect.trySend(
                    HomeSideEffect.NavigateToDriverPicker(
                        customerId = _state.value.homeInputFields.customerId,
                        origin = _state.value.homeInputFields.origin,
                        destination = _state.value.homeInputFields.destination
                    )
                )
            } else {
                _state.value = _state.value.copy(isSearch = false)
                _sideEffect.trySend(HomeSideEffect.ShowNoDriverFound)
            }
        }
    }
}