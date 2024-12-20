package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.repository.DriverInfoRepository
import com.bruno13palhano.data.repository.RidesRepository
import com.bruno13palhano.takeme.ui.shared.base.ActionProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

internal class TravelHistoryActionProcessor(
    private val driverInfoRepository: DriverInfoRepository,
    private val ridesRepository: RidesRepository,
    private val scope: CoroutineScope
) : ActionProcessor<TravelHistoryAction, TravelHistoryState, TravelHistoryEvent> {
    override fun process(
        action: TravelHistoryAction,
        state: TravelHistoryState
    ): Flow<TravelHistoryEvent> {
        return channelFlow {
            when (action) {
                is TravelHistoryAction.OnExpandSelector -> {
                    send(TravelHistoryEvent.ExpandSelector(expandSelector = action.expandSelector))
                }

                is TravelHistoryAction.OnUpdateCurrentDriver -> {
                    send(TravelHistoryEvent.UpdateCurrentDriver(driver = action.driver))
                }

                is TravelHistoryAction.OnGetDrivers -> onGetDrivers()

                is TravelHistoryAction.OnGetCustomerRides -> {
                    onGetCustomerRides(
                        state = state,
                        customerId = action.customerId,
                        driverId = action.driverId
                    )
                }

                is TravelHistoryAction.OnDismissKeyboard -> send(TravelHistoryEvent.DismissKeyboard)

                is TravelHistoryAction.OnNavigateToHome -> send(TravelHistoryEvent.NavigateToHome)
            }

            awaitClose()
        }
    }

    private fun ProducerScope<TravelHistoryEvent>.onGetDrivers() {
        scope.launch {
            driverInfoRepository.getAllDriverInfo().collect {
                send(TravelHistoryEvent.GetDrivers(drivers = it))
            }
        }
    }

    private suspend fun ProducerScope<TravelHistoryEvent>.onGetCustomerRides(
        state: TravelHistoryState,
        customerId: String,
        driverId: Long
    ) {
        if (state.travelHistoryInputFields.isValid()) {
            getCustomerRides(state = state, customerId = customerId, driverId = driverId)
        } else {
            send(TravelHistoryEvent.InvalidFieldError)
        }
    }

    private suspend fun ProducerScope<TravelHistoryEvent>.getCustomerRides(
        state: TravelHistoryState,
        customerId: String,
        driverId: Long
    ) {
        send(
            TravelHistoryEvent.GetCustomerRides(
                customerId = customerId,
                driverId = driverId
            )
        )

        scope.launch {
            val response = ridesRepository.getCustomerRides(
                customerId = customerId,
                driverId = driverId,
                driverName = state.currentDriver.name
            )

            when (response) {
                is Resource.Success -> {
                    response.data?.let { rides ->
                        send(TravelHistoryEvent.UpdateRides(rides = rides))
                    }
                }

                is Resource.ServerResponseError -> {
                    send(
                        TravelHistoryEvent.UpdateResponseError(
                            message = response.remoteErrorResponse?.errorDescription
                        )
                    )
                }

                is Resource.Error -> {
                    send(
                        TravelHistoryEvent.UpdateInternalError(
                            internalError = response.internalError
                        )
                    )
                }
            }
        }
    }
}