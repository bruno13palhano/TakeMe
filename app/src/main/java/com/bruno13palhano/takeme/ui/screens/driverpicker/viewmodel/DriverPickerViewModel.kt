package com.bruno13palhano.takeme.ui.screens.driverpicker.viewmodel

import com.bruno13palhano.data.di.ConfirmRideRep
import com.bruno13palhano.data.di.RideEstimateRep
import com.bruno13palhano.data.repository.ConfirmRideRepository
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.di.DefaultDriverPickerReducer
import com.bruno13palhano.takeme.ui.screens.di.DefaultDriverPickerState
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerAction
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerActionProcessor
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerEvent
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerReducer
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerSideEffect
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerState
import com.bruno13palhano.takeme.ui.shared.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class DriverPickerViewModel @Inject constructor(
    @RideEstimateRep private val rideEstimateRepository: RideEstimateRepository,
    @ConfirmRideRep private val confirmRideRepository: ConfirmRideRepository,
    @DefaultDriverPickerState private val defaultDriverPickerState: DriverPickerState,
    @DefaultDriverPickerReducer private val defaultDriverPickerReducer: DriverPickerReducer
) : BaseViewModel<DriverPickerState, DriverPickerAction, DriverPickerEvent, DriverPickerSideEffect>(
    initialState = defaultDriverPickerState,
    actionProcessor = DriverPickerActionProcessor(
        rideEstimateRepository = rideEstimateRepository,
        confirmRideRepository = confirmRideRepository
    ),
    reducer = defaultDriverPickerReducer
)