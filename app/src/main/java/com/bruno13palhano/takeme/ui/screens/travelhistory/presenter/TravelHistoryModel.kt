package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import androidx.compose.runtime.Immutable
import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.model.Ride
import com.bruno13palhano.takeme.ui.shared.base.ViewAction
import com.bruno13palhano.takeme.ui.shared.base.ViewEvent
import com.bruno13palhano.takeme.ui.shared.base.ViewSideEffect
import com.bruno13palhano.takeme.ui.shared.base.ViewState

@Immutable
internal data class TravelHistoryState(
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
    data class Loading(val isLoading: Boolean) : TravelHistoryEvent
    data class ExpandSelector(val expandSelector: Boolean) : TravelHistoryEvent
    data class UpdateCurrentDriver(val driver: DriverInfo) : TravelHistoryEvent
    data class GetDrivers(val drivers: List<DriverInfo>) : TravelHistoryEvent
    data class UpdateRides(val rides: List<Ride>) : TravelHistoryEvent
    data class Error(val message: String?) : TravelHistoryEvent
    data object InvalidFieldError : TravelHistoryEvent
    data class GetCustomerRides(val customerId: String, val driverId: Long) : TravelHistoryEvent
    data object DismissKeyboard : TravelHistoryEvent
    data object NavigateToHome : TravelHistoryEvent
}

@Immutable
internal sealed interface TravelHistorySideEffect : ViewSideEffect {
    data class ShowError(val message: String?) : TravelHistorySideEffect
    data object InvalidFieldError : TravelHistorySideEffect
    data object DismissKeyboard : TravelHistorySideEffect
    data object NavigateToHome : TravelHistorySideEffect
}

@Immutable
internal sealed interface TravelHistoryAction : ViewAction {
    data class OnLoading(val isLoading: Boolean) : TravelHistoryAction
    data class OnExpandSelector(val expandSelector: Boolean) : TravelHistoryAction
    data class OnUpdateCurrentDriver(val driver: DriverInfo) : TravelHistoryAction
    data object OnGetDrivers : TravelHistoryAction
    data class OnGetCustomerRides(val customerId: String, val driverId: Long) : TravelHistoryAction
    data object OnDismissKeyboard : TravelHistoryAction
    data object OnNavigateToHome : TravelHistoryAction
}