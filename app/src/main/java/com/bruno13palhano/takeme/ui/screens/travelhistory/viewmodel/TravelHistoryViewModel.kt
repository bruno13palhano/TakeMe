package com.bruno13palhano.takeme.ui.screens.travelhistory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bruno13palhano.data.di.DriverInfoRep
import com.bruno13palhano.data.di.RidesRep
import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.repository.DriverInfoRepository
import com.bruno13palhano.data.repository.RidesRepository
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryEvent
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistorySideEffect
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryState
import com.bruno13palhano.takeme.ui.shared.base.ContainerMVI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@HiltViewModel
internal class TravelHistoryViewModel @Inject constructor(
    @DriverInfoRep private val driverInfoRepository: DriverInfoRepository,
    @RidesRep private val ridesRepository: RidesRepository
) : ViewModel() {
    val container = ContainerMVI<TravelHistoryState, TravelHistorySideEffect>(
        initialState = TravelHistoryState.initialState,
        scope = viewModelScope
    )

    fun sendEvent(event: TravelHistoryEvent) {
        when (event) {
            is TravelHistoryEvent.DismissKeyboard -> container.intent {
                sendSideEffect(TravelHistorySideEffect.DismissKeyboard)
            }

            is TravelHistoryEvent.ExpandSelector -> container.intent {
                reduce {
                    copy(
                        expandSelector = !state.value.expandSelector
                    )
                }
            }

            is TravelHistoryEvent.GetCustomerRides -> getCustomerRides(
                customerId = event.customerId,
                driverId = event.driverId
            )

            is TravelHistoryEvent.GetDrivers -> container.intent {
                val drivers = driverInfoRepository.getAllDriverInfo().firstOrNull()
                val currentDriver = drivers?.firstOrNull() ?: DriverInfo.empty

                reduce {
                    copy(
                        start = false,
                        isLoading = false,
                        currentDriver = currentDriver,
                        drivers = drivers ?: emptyList()
                    )
                }
            }

            is TravelHistoryEvent.NavigateToHome -> container.intent {
                reduce { copy(isLoading = false) }
                sendSideEffect(TravelHistorySideEffect.NavigateToHome)
            }

            is TravelHistoryEvent.UpdateCurrentDriver -> container.intent {
                reduce { copy(currentDriver = event.driver) }
            }
        }
    }

    private fun getCustomerRides(customerId: String, driverId: Long) = container.intent {
        if (state.value.travelHistoryInputFields.isValid()) {
            reduce {
                copy(
                    isLoading = true,
                    isFieldInvalid = false,
                    customerId = customerId,
                    driverId = driverId
                )
            }

            val response = ridesRepository.getCustomerRides(
                customerId = customerId,
                driverId = driverId,
                driverName = state.value.currentDriver.name
            )

            when (response) {
                is Resource.Success -> {
                    response.data?.let { rides ->
                        reduce { copy(isLoading = false, rides = rides) }
                    }
                }

                is Resource.ServerResponseError -> {
                    reduce { copy(isLoading = false, rides = emptyList()) }
                    sendSideEffect(
                        TravelHistorySideEffect.ShowResponseError(
                            message = response.remoteErrorResponse?.errorDescription
                        )
                    )
                }

                is Resource.Error -> {
                    reduce { copy(isLoading = false, rides = emptyList()) }
                    sendSideEffect(
                        TravelHistorySideEffect.ShowInternalError(
                            internalError = response.internalError
                        )
                    )
                }
            }
        } else {
            container.intent {
                reduce { copy(isLoading = false, isFieldInvalid = true) }
                sendSideEffect(TravelHistorySideEffect.InvalidFieldError)
            }
        }
    }
}