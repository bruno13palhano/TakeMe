package com.bruno13palhano.takeme.ui.screens.travelhistory.viewmodel

import androidx.lifecycle.viewModelScope
import com.bruno13palhano.data.di.Dispatcher
import com.bruno13palhano.data.di.DriverInfoRep
import com.bruno13palhano.data.di.RidesRep
import com.bruno13palhano.data.di.TakeMeDispatchers
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.repository.DriverInfoRepository
import com.bruno13palhano.data.repository.RidesRepository
import com.bruno13palhano.takeme.ui.screens.di.DefaultTravelHistoryReducer
import com.bruno13palhano.takeme.ui.screens.di.DefaultTravelHistoryState
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryAction
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryEvent
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryReducer
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistorySideEffect
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryState
import com.bruno13palhano.takeme.ui.shared.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class TravelHistoryViewModel @Inject constructor(
    @DriverInfoRep private val driverInfoRepository: DriverInfoRepository,
    @RidesRep private val ridesRepository: RidesRepository,
    @DefaultTravelHistoryState private val initialTravelHistoryState: TravelHistoryState,
    @DefaultTravelHistoryReducer private val travelHistoryReducer: TravelHistoryReducer,
    @Dispatcher(TakeMeDispatchers.IO) private val dispatcher: CoroutineDispatcher
) : BaseViewModel<TravelHistoryState, TravelHistoryAction, TravelHistoryEvent, TravelHistorySideEffect>(
    initialState = initialTravelHistoryState,
    reducer = travelHistoryReducer
) {
    override fun onAction(action: TravelHistoryAction) {
        when (action) {
            is TravelHistoryAction.OnExpandSelector -> {
                sendEvent(
                    event = TravelHistoryEvent.ExpandSelector(
                        expandSelector = action.expandSelector
                    )
                )
            }

            is TravelHistoryAction.OnUpdateCurrentDriver -> {
                sendEvent(event = TravelHistoryEvent.UpdateCurrentDriver(driver = action.driver))
            }

            is TravelHistoryAction.OnGetDrivers -> onGetDrivers()

            is TravelHistoryAction.OnGetCustomerRides -> {
                onGetCustomerRides(customerId = action.customerId, driverId = action.driverId)
            }

            is TravelHistoryAction.OnDismissKeyboard -> {
                sendEvent(event = TravelHistoryEvent.DismissKeyboard)
            }

            is TravelHistoryAction.OnNavigateToHome -> {
                sendEvent(event = TravelHistoryEvent.NavigateToHome)
            }
        }
    }

    private fun onGetDrivers() {
        viewModelScope.launch(SupervisorJob() + dispatcher) {
            driverInfoRepository.getAllDriverInfo().collect {
                sendEvent(event = TravelHistoryEvent.GetDrivers(drivers = it))
            }
        }
    }

    private fun onGetCustomerRides(customerId: String, driverId: Long) {
        if (state.value.travelHistoryInputFields.isValid()) {
            getCustomerRides(customerId = customerId, driverId = driverId)
        } else {
            sendEvent(event = TravelHistoryEvent.InvalidFieldError)
        }
    }

    private fun getCustomerRides(customerId: String, driverId: Long) {
        sendEvent(
            event = TravelHistoryEvent.GetCustomerRides(
                customerId = customerId,
                driverId = driverId
            )
        )

        viewModelScope.launch(SupervisorJob() + dispatcher) {
            val response = ridesRepository.getCustomerRides(
                customerId = customerId,
                driverId = state.value.currentDriver.id,
                driverName = state.value.currentDriver.name
            )

            when (response) {
                is Resource.Success -> {
                    response.data?.let { rides ->
                        sendEvent(event = TravelHistoryEvent.UpdateRides(rides = rides))
                    }
                }

                is Resource.ServerResponseError -> {
                    sendEvent(
                        event = TravelHistoryEvent.UpdateResponseError(
                            message = response.remoteErrorResponse?.errorDescription
                        )
                    )
                }

                is Resource.Error -> {
                    sendEvent(
                        event = TravelHistoryEvent.UpdateInternalError(
                            internalError = response.internalError
                        )
                    )
                }
            }
        }
    }
}