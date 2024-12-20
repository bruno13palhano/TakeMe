package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import com.bruno13palhano.data.model.ConfirmRide
import com.bruno13palhano.data.model.RequestConfirmRide
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.repository.ConfirmRideRepository
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.shared.base.ActionProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

internal class DriverPickerActionProcessor(
    private val rideEstimateRepository: RideEstimateRepository,
    private val confirmRideRepository: ConfirmRideRepository,
    private val scope: CoroutineScope
) : ActionProcessor<DriverPickerAction, DriverPickerState, DriverPickerEvent> {
    override fun process(
        action: DriverPickerAction,
        state: DriverPickerState
    ): Flow<DriverPickerEvent> {
        return channelFlow {
            when (action) {
                is DriverPickerAction.OnGetLastRideEstimate -> onGetLastRideEstimate()

                is DriverPickerAction.OnUpdateCustomerParams -> send(
                    DriverPickerEvent.UpdateCustomerParams(
                        customerId = action.customerId,
                        origin = action.origin,
                        destination = action.destination
                    )
                )

                is DriverPickerAction.OnChooseDriver -> onChooseDriver(
                    state = state,
                    driverId = action.driverId,
                    driverName = action.driverName,
                    value = action.value
                )

                is DriverPickerAction.OnNavigateToTravelHistory -> send(
                    DriverPickerEvent.NavigateToTravelHistory
                )

                is DriverPickerAction.OnNavigateBack -> send(DriverPickerEvent.NavigateBack)
            }

            awaitClose()
        }
    }

    private fun ProducerScope<DriverPickerEvent>.onGetLastRideEstimate() {
        scope.launch {
            rideEstimateRepository.getLastRideEstimate().collect {
                it?.let { rideEstimate ->
                    send(
                        DriverPickerEvent.UpdateRideEstimate(rideEstimate = rideEstimate)
                    )
                }
            }
        }
    }

    private suspend fun ProducerScope<DriverPickerEvent>.onChooseDriver(
        state: DriverPickerState,
        driverId: Long,
        driverName: String,
        value: Float
    ) {
        send(
            DriverPickerEvent.ChooseDriver(
                driverId = driverId,
                driverName = driverName,
                value = value
            )
        )

        scope.launch {
            val response = confirmRideRepository.confirmRide(
                confirmRide = RequestConfirmRide(
                    customerId = state.customerId,
                    origin = state.origin,
                    destination = state.destination,
                    distance = state.rideEstimate.distance,
                    duration = state.rideEstimate.duration,
                    driverId = driverId,
                    driverName = driverName,
                    value = value
                )
            )

            processChooseDriverResponse(response = response)
        }
    }

    private suspend fun ProducerScope<DriverPickerEvent>.processChooseDriverResponse(
        response: Resource<ConfirmRide>
    ) {
        when (response) {
            is Resource.Success -> successDriverResponse(response = response)

            is Resource.ServerResponseError -> {
                send(
                    DriverPickerEvent.UpdateResponseError(
                        message = response.remoteErrorResponse!!.errorDescription
                    )
                )
            }

            is Resource.Error -> {
                send(
                    DriverPickerEvent.UpdateInternalError(
                        internalError = response.internalError
                    )
                )
            }
        }
    }

    private suspend fun ProducerScope<DriverPickerEvent>.successDriverResponse(
        response: Resource<ConfirmRide>
    ) {
        response.data?.let {
            if (it.success) {
                send(DriverPickerEvent.NavigateToTravelHistory)
            } else {
                send(
                    DriverPickerEvent.UpdateResponseError(message = response.internalError?.name)
                )
            }
        }
    }
}