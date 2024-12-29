package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.repository.DriverInfoRepository
import com.bruno13palhano.data.repository.RidesRepository
import com.bruno13palhano.takeme.ui.shared.base.ActionProcessor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

internal class TravelHistoryActionProcessor(
    private val driverInfoRepository: DriverInfoRepository,
    private val ridesRepository: RidesRepository
) : ActionProcessor<TravelHistoryAction, TravelHistoryState, TravelHistoryEvent> {
    override fun process(
        action: TravelHistoryAction,
        state: TravelHistoryState
    ): Flow<TravelHistoryEvent> {
        return flow {
            when (action) {
                is TravelHistoryAction.OnExpandSelector -> {
                    emit(TravelHistoryEvent.ExpandSelector(expandSelector = action.expandSelector))
                }

                is TravelHistoryAction.OnUpdateCurrentDriver -> {
                    emit(TravelHistoryEvent.UpdateCurrentDriver(driver = action.driver))
                }

                is TravelHistoryAction.OnGetDrivers -> onGetDrivers()

                is TravelHistoryAction.OnGetCustomerRides -> {
                    onGetCustomerRides(
                        state = state,
                        customerId = action.customerId,
                        driverId = action.driverId
                    )
                }

                is TravelHistoryAction.OnDismissKeyboard -> emit(TravelHistoryEvent.DismissKeyboard)

                is TravelHistoryAction.OnNavigateToHome -> emit(TravelHistoryEvent.NavigateToHome)
            }
        }
    }

    private suspend fun FlowCollector<TravelHistoryEvent>.onGetDrivers() {
        driverInfoRepository.getAllDriverInfo().collect {
            emit(TravelHistoryEvent.GetDrivers(drivers = it))
        }
    }

    private suspend fun FlowCollector<TravelHistoryEvent>.onGetCustomerRides(
        state: TravelHistoryState,
        customerId: String,
        driverId: Long
    ) {
        if (state.travelHistoryInputFields.isValid()) {
            getCustomerRides(state = state, customerId = customerId, driverId = driverId)
        } else {
            emit(TravelHistoryEvent.InvalidFieldError)
        }
    }

    private suspend fun FlowCollector<TravelHistoryEvent>.getCustomerRides(
        state: TravelHistoryState,
        customerId: String,
        driverId: Long
    ) {
        emit(
            TravelHistoryEvent.GetCustomerRides(
                customerId = customerId,
                driverId = driverId
            )
        )

        val response = ridesRepository.getCustomerRides(
            customerId = customerId,
            driverId = driverId,
            driverName = state.currentDriver.name
        )

        when (response) {
            is Resource.Success -> {
                response.data?.let { rides ->
                    emit(TravelHistoryEvent.UpdateRides(rides = rides))
                }
            }

            is Resource.ServerResponseError -> {
                emit(
                    TravelHistoryEvent.UpdateResponseError(
                        message = response.remoteErrorResponse?.errorDescription
                    )
                )
            }

            is Resource.Error -> {
                emit(
                    TravelHistoryEvent.UpdateInternalError(
                        internalError = response.internalError
                    )
                )
            }
        }
    }
}