package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import androidx.compose.runtime.Immutable
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.takeme.ui.shared.base.ViewAction
import com.bruno13palhano.takeme.ui.shared.base.ViewEvent
import com.bruno13palhano.takeme.ui.shared.base.ViewSideEffect
import com.bruno13palhano.takeme.ui.shared.base.ViewState

@Immutable
internal data class DriverPickerState(
    val isLoading: Boolean,
    val isMapLoading: Boolean,
    val rideEstimate: RideEstimate,
    val customerId: String,
    val origin: String,
    val destination: String,
    val driverId: Long,
    val driverName: String,
    val value: Float,
) : ViewState {
    companion object {
        val initialState = DriverPickerState(
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
internal sealed interface DriverPickerEvent : ViewEvent {
    data class UpdateCustomerParams(
        val customerId: String,
        val origin: String,
        val destination: String
    ) : DriverPickerEvent
    data class Error(val message: String?) : DriverPickerEvent
    data class UpdateRideEstimate(val rideEstimate: RideEstimate) : DriverPickerEvent
    data class ChooseDriver(
        val driverId: Long,
        val driverName: String,
        val value: Float
    ) : DriverPickerEvent
    data object NavigateToTravelHistory : DriverPickerEvent
    data object NavigateBack : DriverPickerEvent
}

@Immutable
internal sealed interface DriverPickerSideEffect : ViewSideEffect {
    data  class ShowError(val message: String?) : DriverPickerSideEffect
    data object NavigateToTravelHistory : DriverPickerSideEffect
    data object NavigateBack : DriverPickerSideEffect
}

@Immutable
internal sealed interface DriverPickerAction : ViewAction {
    data class OnUpdateCustomerParams(
        val customerId: String,
        val origin: String,
        val destination: String
    ) : DriverPickerAction
    data object OnGetLastRideEstimate : DriverPickerAction
    data class OnChooseDriver(
        val driverId: Long,
        val driverName: String,
        val value: Float
    ) : DriverPickerAction
    data object OnNavigateToTravelHistory : DriverPickerAction
    data object OnNavigateBack : DriverPickerAction
}