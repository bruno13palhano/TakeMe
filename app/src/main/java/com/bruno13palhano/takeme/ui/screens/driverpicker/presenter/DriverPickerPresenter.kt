package com.bruno13palhano.takeme.ui.screens.driverpicker.presenter

import com.bruno13palhano.data.di.ConfirmRideRep
import com.bruno13palhano.data.di.IOScope
import com.bruno13palhano.data.di.RideEstimateRep
import com.bruno13palhano.data.repository.ConfirmRideRepository
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.di.DefaultDriverPickerReducer
import com.bruno13palhano.takeme.ui.screens.di.DefaultDriverPickerState
import com.bruno13palhano.takeme.ui.shared.base.BasePresenter
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

internal class DriverPickerPresenter @Inject constructor(
    @RideEstimateRep private val rideEstimateRepository: RideEstimateRepository,
    @ConfirmRideRep private val confirmRideRepository: ConfirmRideRepository,
    @DefaultDriverPickerState initialState: DriverPickerState,
    @DefaultDriverPickerReducer defaultDriverPickerReducer: DriverPickerReducer,
    @IOScope private val ioScope: CoroutineScope
) : BasePresenter<DriverPickerAction, DriverPickerEvent, DriverPickerState, DriverPickerSideEffect>(
    initialState = initialState,
    actionProcessor = DriverPickerActionProcessor(
        rideEstimateRepository = rideEstimateRepository,
        confirmRideRepository = confirmRideRepository,
        scope = ioScope
    ),
    reducer = defaultDriverPickerReducer,
    scope = ioScope
)