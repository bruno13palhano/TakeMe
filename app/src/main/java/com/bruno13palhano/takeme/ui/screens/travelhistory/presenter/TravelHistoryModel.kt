package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import androidx.compose.runtime.Immutable
import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.data.model.Ride
import com.bruno13palhano.takeme.ui.shared.base.ViewEvent
import com.bruno13palhano.takeme.ui.shared.base.ViewSideEffect
import com.bruno13palhano.takeme.ui.shared.base.ViewState

@Immutable
internal data class TravelHistoryState(
    val start: Boolean,
    val isLoading: Boolean,
    val isFieldInvalid: Boolean,
    val customerId: String,
    val driverId: Long,
    val travelHistoryInputFields: TravelHistoryInputFields,
    val expandSelector: Boolean,
    val currentDriver: DriverInfo,
    val drivers: List<DriverInfo>,
    val rides: List<Ride>
) : ViewState {
    companion object {
        val initialState = TravelHistoryState(
            start = true,
            isLoading = false,
            isFieldInvalid = false,
            customerId = "",
            driverId = 0,
            travelHistoryInputFields = TravelHistoryInputFields(),
            expandSelector = false,
            currentDriver = DriverInfo.empty,
            drivers = emptyList(),
            rides = emptyList()
        )
    }
}

@Immutable
internal sealed interface TravelHistoryEvent : ViewEvent {
    data class ExpandSelector(val expandSelector: Boolean) : TravelHistoryEvent
    data class UpdateCurrentDriver(val driver: DriverInfo) : TravelHistoryEvent
    data object GetDrivers : TravelHistoryEvent
    data class GetCustomerRides(val customerId: String, val driverId: Long) : TravelHistoryEvent
    data object DismissKeyboard : TravelHistoryEvent
    data object NavigateToHome : TravelHistoryEvent
}

@Immutable
internal sealed interface TravelHistorySideEffect : ViewSideEffect {
    data class ShowResponseError(val message: String?) : TravelHistorySideEffect
    data class ShowInternalError(val internalError: InternalError?) : TravelHistorySideEffect
    data object InvalidFieldError : TravelHistorySideEffect
    data object DismissKeyboard : TravelHistorySideEffect
    data object NavigateToHome : TravelHistorySideEffect
}