package com.bruno13palhano.takeme.ui.screens.driverpicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno13palhano.data.di.ConfirmRideRep
import com.bruno13palhano.data.di.RideEstimateRep
import com.bruno13palhano.data.model.ConfirmRide
import com.bruno13palhano.data.model.RequestConfirmRide
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.repository.ConfirmRideRepository
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerEvent
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerSideEffect
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DriverPickerViewModel @Inject constructor(
    @RideEstimateRep private val rideEstimateRepository: RideEstimateRepository,
    @ConfirmRideRep private val confirmRideRepository: ConfirmRideRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DriverPickerState.initialState)
    val state = _state

    private val events = MutableSharedFlow<DriverPickerEvent>(extraBufferCapacity = 20)

    private val _sideEffect = Channel<DriverPickerSideEffect>(capacity = Channel.CONFLATED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun sendEvent(event: DriverPickerEvent) {
        if (!events.tryEmit(event)) {
            throw IllegalStateException("Event buffer overflow")
        }
    }

    fun handleEvents() {
        viewModelScope.launch {
            events.collect { event ->
                when (event) {
                    is DriverPickerEvent.ChooseDriver -> chooseDriver(
                        driverId = event.driverId,
                        driverName = event.driverName,
                        value = event.value
                    )

                    is DriverPickerEvent.NavigateBack -> {
                        _state.value = _state.value.copy(isLoading = false)
                        _sideEffect.trySend(DriverPickerSideEffect.NavigateBack)
                    }

                    is DriverPickerEvent.UpdateCustomerParams -> {
                        _state.value = _state.value.copy(
                            start = false,
                            customerId = event.customerId,
                            origin = event.origin,
                            destination = event.destination
                        )
                    }

                    is DriverPickerEvent.UpdateRideEstimate -> {
                        val rideEstimate = rideEstimateRepository.getLastRideEstimate()
                        rideEstimate?.let {
                            _state.value = _state.value.copy(rideEstimate = it)
                        }
                    }
                }
            }
        }
    }

    private suspend fun chooseDriver(driverId: Long, driverName: String, value: Float) {
        _state.value = _state.value.copy(
            isLoading = true,
            driverId = driverId,
            driverName = driverName,
            value = value
        )

        val response = confirmRideRepository.confirmRide(
            confirmRide = RequestConfirmRide(
                customerId = _state.value.customerId,
                origin = _state.value.origin,
                destination = _state.value.destination,
                distance = _state.value.rideEstimate.distance,
                duration = _state.value.rideEstimate.duration,
                driverId = driverId,
                driverName = driverName,
                value = value
            )
        )

        processChooseDriverResponse(response = response)
    }

    private fun processChooseDriverResponse(response: Resource<ConfirmRide>) {
        when (response) {
            is Resource.Success -> successDriverResponse(response = response)

            is Resource.ServerResponseError -> {
                updateResponseError(
                    message = response.remoteErrorResponse!!.errorDescription
                )
            }

            is Resource.Error -> {
                _state.value = _state.value.copy(isLoading = false)
                _sideEffect.trySend(
                    DriverPickerSideEffect.ShowInternalError(
                        internalError = response.internalError
                    )
                )
            }
        }
    }

    private fun successDriverResponse(response: Resource<ConfirmRide>) {
        response.data?.let {
            if (it.success) {
                _sideEffect.trySend(DriverPickerSideEffect.NavigateToTravelHistory)
            } else {
                updateResponseError(message = response.internalError?.name)
            }
        }
    }

    private fun updateResponseError(message: String?) {
        _state.value = _state.value.copy(isLoading = false)
        _sideEffect.trySend(DriverPickerSideEffect.ShowResponseError(message = message))
    }
}