package com.bruno13palhano.takeme.ui.screens.travelhistory.presenter

import com.bruno13palhano.data.di.DriverInfoRep
import com.bruno13palhano.data.di.RidesRep
import com.bruno13palhano.data.repository.DriverInfoRepository
import com.bruno13palhano.data.repository.RidesRepository
import com.bruno13palhano.takeme.ui.screens.di.DefaultTravelHistoryReducer
import com.bruno13palhano.takeme.ui.screens.di.DefaultTravelHistoryState
import com.bruno13palhano.takeme.ui.shared.base.BasePresenter
import javax.inject.Inject

internal class TravelHistoryPresenter @Inject constructor(
    @DriverInfoRep private val driverInfoRepository: DriverInfoRepository,
    @RidesRep private val ridesRepository: RidesRepository,
    @DefaultTravelHistoryState private val initialTravelHistoryState: TravelHistoryState,
    @DefaultTravelHistoryReducer private val  travelHistoryReducer: TravelHistoryReducer
) : BasePresenter<TravelHistoryAction, TravelHistoryEvent, TravelHistoryState, TravelHistorySideEffect>(
    initialState = initialTravelHistoryState,
    actionProcessor = TravelHistoryActionProcessor(
        driverInfoRepository = driverInfoRepository,
        ridesRepository = ridesRepository
    ),
    reducer = travelHistoryReducer
)