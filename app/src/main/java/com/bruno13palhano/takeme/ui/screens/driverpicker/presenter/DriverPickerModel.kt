package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import androidx.compose.runtime.Immutable
import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.data.model.RideEstimate

@Immutable
internal data class DriverPickerState(
    val start: Boolean,
    val isLoading: Boolean,
    val isMapLoading: Boolean,
    val rideEstimate: RideEstimate,
    val customerId: String,
    val origin: String,
    val destination: String,
    val driverId: Long,
    val driverName: String,
    val value: Float,
) {
    companion object {
        val initialState = DriverPickerState(
            start = true,
            isLoading = false,
            isMapLoading = true,
            rideEstimate = RideEstimate.empty,
            customerId = "",
            origin = "",
            destination = "",
            driverId = 0,
            driverName = "",
            value = 0.0f
        )
    }
}

@Immutable
internal sealed interface DriverPickerEvent {
    data class UpdateCustomerParams(
        val customerId: String,
        val origin: String,
        val destination: String
    ) : DriverPickerEvent
    data object UpdateRideEstimate : DriverPickerEvent
    data class ChooseDriver(
        val driverId: Long,
        val driverName: String,
        val value: Float
    ) : DriverPickerEvent
    data object NavigateBack : DriverPickerEvent
}

@Immutable
internal sealed interface DriverPickerSideEffect {
    data  class ShowResponseError(val message: String?) : DriverPickerSideEffect
    data class ShowInternalError(val internalError: InternalError?) : DriverPickerSideEffect
    data object NavigateToTravelHistory : DriverPickerSideEffect
    data object NavigateBack : DriverPickerSideEffect
}