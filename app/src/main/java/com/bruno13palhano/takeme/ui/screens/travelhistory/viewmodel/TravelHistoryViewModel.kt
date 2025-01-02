package com.bruno13palhano.takeme.ui.screens.travelhistory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno13palhano.data.di.DriverInfoRep
import com.bruno13palhano.data.di.RidesRep
import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.repository.DriverInfoRepository
import com.bruno13palhano.data.repository.RidesRepository
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryEvent
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistorySideEffect
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TravelHistoryViewModel @Inject constructor(
    @DriverInfoRep private val driverInfoRepository: DriverInfoRepository,
    @RidesRep private val ridesRepository: RidesRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TravelHistoryState.initialState)
    val state: StateFlow<TravelHistoryState> = _state

    private val events = MutableSharedFlow<TravelHistoryEvent>(extraBufferCapacity = 20)

    private val _sideEffect = Channel<TravelHistorySideEffect>(capacity = Channel.CONFLATED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun sendEvent(event: TravelHistoryEvent) {
        if (!events.tryEmit(event)) {
            throw IllegalStateException("Event buffer overflow")
        }
    }

    fun handleEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is TravelHistoryEvent.DismissKeyboard -> {
                        _sideEffect.trySend(TravelHistorySideEffect.DismissKeyboard)
                    }

                    is TravelHistoryEvent.ExpandSelector -> {
                        _state.value = _state.value.copy(
                            expandSelector = !_state.value.expandSelector
                        )
                    }

                    is TravelHistoryEvent.GetCustomerRides -> getCustomerRides(
                        customerId = event.customerId,
                        driverId = event.driverId
                    )

                    is TravelHistoryEvent.GetDrivers -> {
                        val drivers = driverInfoRepository.getAllDriverInfo().firstOrNull()
                        val currentDriver = drivers?.firstOrNull() ?: DriverInfo.empty

                        _state.value = _state.value.copy(
                            start = false,
                            isLoading = false,
                            currentDriver = currentDriver,
                            drivers = drivers ?: emptyList()
                        )
                    }

                    is TravelHistoryEvent.NavigateToHome -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _sideEffect.trySend(TravelHistorySideEffect.NavigateToHome)
                    }

                    is TravelHistoryEvent.UpdateCurrentDriver -> {
                        _state.value = _state.value.copy(currentDriver = event.driver)
                    }
                }
            }
        }
    }

    private suspend fun getCustomerRides(customerId: String, driverId: Long) {
        if (_state.value.travelHistoryInputFields.isValid()) {
            _state.value = _state.value.copy(
                isLoading = true,
                isFieldInvalid = false,
                customerId = customerId,
                driverId = driverId
            )

            val response = ridesRepository.getCustomerRides(
                customerId = customerId,
                driverId = driverId,
                driverName = _state.value.currentDriver.name
            )

            when (response) {
                is Resource.Success -> {
                    response.data?.let { rides ->
                        _state.value = _state.value.copy(isLoading = false, rides = rides)
                    }
                }

                is Resource.ServerResponseError -> {
                    _state.value = _state.value.copy(isLoading = false, rides = emptyList())
                    _sideEffect.trySend(
                        TravelHistorySideEffect.ShowResponseError(
                            message = response.remoteErrorResponse?.errorDescription
                        )
                    )
                }

                is Resource.Error -> {
                    _state.value = _state.value.copy(isLoading = false, rides = emptyList())
                    _sideEffect.trySend(
                        TravelHistorySideEffect.ShowInternalError(
                            internalError = response.internalError
                        )
                    )
                }
            }
        } else {
            _state.value = _state.value.copy(isLoading = false, isFieldInvalid = true)
            _sideEffect.trySend(TravelHistorySideEffect.InvalidFieldError)
        }
    }
}