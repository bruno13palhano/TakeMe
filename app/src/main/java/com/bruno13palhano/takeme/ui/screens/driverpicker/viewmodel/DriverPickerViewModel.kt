package com.bruno13palhano.takeme.ui.screens.driverpicker.viewmodel

import androidx.lifecycle.viewModelScope
import com.bruno13palhano.data.di.ConfirmRideRep
import com.bruno13palhano.data.di.Dispatcher
import com.bruno13palhano.data.di.RideEstimateRep
import com.bruno13palhano.data.di.TakeMeDispatchers
import com.bruno13palhano.data.model.ConfirmRide
import com.bruno13palhano.data.model.RequestConfirmRide
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.repository.ConfirmRideRepository
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerAction
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerEvent
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerReducer
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerSideEffect
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerState
import com.bruno13palhano.takeme.ui.shared.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class DriverPickerViewModel @Inject constructor(
    @RideEstimateRep private val rideEstimateRepository: RideEstimateRepository,
    @ConfirmRideRep private val confirmRideRepository: ConfirmRideRepository,
    @Dispatcher(TakeMeDispatchers.IO) private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<DriverPickerState, DriverPickerAction, DriverPickerEvent, DriverPickerSideEffect>(
    initialState = DriverPickerState.initialState,
    reducer = DriverPickerReducer()
) {
    override fun onAction(action: DriverPickerAction) {
        when (action) {
            is DriverPickerAction.OnGetLastRideEstimate -> onGetLastRideEstimate()

            is DriverPickerAction.OnUpdateCustomerParams -> sendEvent(
                event = DriverPickerEvent.UpdateCustomerParams(
                    customerId = action.customerId,
                    origin = action.origin,
                    destination = action.destination
                )
            )

            is DriverPickerAction.OnChooseDriver -> onChooseDriver(
                driverId = action.driverId,
                driverName = action.driverName,
                value = action.value
            )

            is DriverPickerAction.OnNavigateToTravelHistory -> sendEvent(
                event = DriverPickerEvent.NavigateToTravelHistory
            )

            is DriverPickerAction.OnNavigateBack -> sendEvent(
                event = DriverPickerEvent.NavigateBack
            )
        }
    }

    private fun onGetLastRideEstimate() {
        viewModelScope.launch(SupervisorJob() + dispatcher) {
            rideEstimateRepository.getLastRideEstimate().collect {
                it?.let { rideEstimate ->
                    sendEvent(
                        event = DriverPickerEvent.UpdateRideEstimate(rideEstimate = rideEstimate)
                    )
                }
            }
        }
    }

    private fun onChooseDriver(
        driverId: Long,
        driverName: String,
        value: Float
    ) {
        sendEvent(
            event = DriverPickerEvent.ChooseDriver(
                driverId = driverId,
                driverName = driverName,
                value = value
            )
        )

        viewModelScope.launch(SupervisorJob() + dispatcher) {
            val response = confirmRideRepository.confirmRide(
                confirmRide = RequestConfirmRide(
                    customerId = state.value.customerId,
                    origin = state.value.origin,
                    destination = state.value.destination,
                    distance = state.value.rideEstimate.distance,
                    duration = state.value.rideEstimate.duration,
                    driverId = driverId,
                    driverName = driverName,
                    value = value
                )
            )

            processChooseDriverResponse(response = response)
        }
    }

    private fun processChooseDriverResponse(response: Resource<ConfirmRide>) {
        when (response) {
            is Resource.Success -> successDriverResponse(response = response)

            is Resource.ServerResponseError -> {
                sendEvent(
                    event = DriverPickerEvent.Error(
                        message = response.remoteErrorResponse!!.errorDescription
                    )
                )
            }

            is Resource.Error -> {
                sendEvent(event = DriverPickerEvent.Error(message = response.message))
            }
        }
    }

    private fun successDriverResponse(response: Resource<ConfirmRide>) {
        response.data?.let {
            if (it.success) {
                sendEvent(event = DriverPickerEvent.NavigateToTravelHistory)
            } else {
                sendEvent(
                    event = DriverPickerEvent.Error(message = response.message)
                )
            }
        }
    }
}