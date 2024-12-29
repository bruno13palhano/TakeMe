package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import com.bruno13palhano.data.model.ConfirmRide
import com.bruno13palhano.data.model.RequestConfirmRide
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.repository.ConfirmRideRepository
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.shared.base.ActionProcessor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

internal class DriverPickerActionProcessor(
    private val rideEstimateRepository: RideEstimateRepository,
    private val confirmRideRepository: ConfirmRideRepository
) : ActionProcessor<DriverPickerAction, DriverPickerState, DriverPickerEvent> {
    override fun process(
        action: DriverPickerAction,
        state: DriverPickerState
    ): Flow<DriverPickerEvent> {
        return flow {
            when (action) {
                is DriverPickerAction.OnGetLastRideEstimate -> onGetLastRideEstimate()

                is DriverPickerAction.OnUpdateCustomerParams -> emit(
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

                is DriverPickerAction.OnNavigateToTravelHistory -> emit(
                    DriverPickerEvent.NavigateToTravelHistory
                )

                is DriverPickerAction.OnNavigateBack -> emit(DriverPickerEvent.NavigateBack)
            }
        }
    }

    private suspend fun FlowCollector<DriverPickerEvent>.onGetLastRideEstimate() {
        rideEstimateRepository.getLastRideEstimate().collect {
            it?.let { rideEstimate ->
                emit(
                    DriverPickerEvent.UpdateRideEstimate(rideEstimate = rideEstimate)
                )
            }
        }
    }

    private suspend fun FlowCollector<DriverPickerEvent>.onChooseDriver(
        state: DriverPickerState,
        driverId: Long,
        driverName: String,
        value: Float
    ) {
        emit(
            DriverPickerEvent.ChooseDriver(
                driverId = driverId,
                driverName = driverName,
                value = value
            )
        )

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

    private suspend fun FlowCollector<DriverPickerEvent>.processChooseDriverResponse(
        response: Resource<ConfirmRide>
    ) {
        when (response) {
            is Resource.Success -> successDriverResponse(response = response)

            is Resource.ServerResponseError -> {
                emit(
                    DriverPickerEvent.UpdateResponseError(
                        message = response.remoteErrorResponse!!.errorDescription
                    )
                )
            }

            is Resource.Error -> {
                emit(
                    DriverPickerEvent.UpdateInternalError(
                        internalError = response.internalError
                    )
                )
            }
        }
    }

    private suspend fun FlowCollector<DriverPickerEvent>.successDriverResponse(
        response: Resource<ConfirmRide>
    ) {
        response.data?.let {
            if (it.success) {
                emit(DriverPickerEvent.NavigateToTravelHistory)
            } else {
                emit(
                    DriverPickerEvent.UpdateResponseError(message = response.internalError?.name)
                )
            }
        }
    }
}