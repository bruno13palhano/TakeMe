package com.bruno13palhano.takeme.ui.screens.travelhistory.viewmodel

import com.bruno13palhano.data.di.DriverInfoRep
import com.bruno13palhano.data.di.IOScope
import com.bruno13palhano.data.di.RidesRep
import com.bruno13palhano.data.repository.DriverInfoRepository
import com.bruno13palhano.data.repository.RidesRepository
import com.bruno13palhano.takeme.ui.screens.di.DefaultTravelHistoryReducer
import com.bruno13palhano.takeme.ui.screens.di.DefaultTravelHistoryState
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryActionProcessor
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryAction
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryEvent
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryReducer
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistorySideEffect
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryState
import com.bruno13palhano.takeme.ui.shared.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@HiltViewModel
internal class TravelHistoryViewModel @Inject constructor(
    @DriverInfoRep private val driverInfoRepository: DriverInfoRepository,
    @RidesRep private val ridesRepository: RidesRepository,
    @DefaultTravelHistoryState private val initialTravelHistoryState: TravelHistoryState,
    @DefaultTravelHistoryReducer private val travelHistoryReducer: TravelHistoryReducer,
    @IOScope private val ioScope: CoroutineScope,
) : BaseViewModel<TravelHistoryState, TravelHistoryAction, TravelHistoryEvent, TravelHistorySideEffect>(
    initialState = initialTravelHistoryState,
    actionProcessor = TravelHistoryActionProcessor(
        driverInfoRepository = driverInfoRepository,
        ridesRepository = ridesRepository,
        scope = ioScope
    ),
    reducer = travelHistoryReducer
)