package com.bruno13palhano.takeme.ui.screens.home.presenter

import com.bruno13palhano.data.di.IOScope
import com.bruno13palhano.data.di.RideEstimateRep
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.di.DefaultHomeReducer
import com.bruno13palhano.takeme.ui.screens.di.DefaultHomeState
import com.bruno13palhano.takeme.ui.shared.base.BasePresenter
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

internal class HomePresenter @Inject constructor(
    @RideEstimateRep private val repository: RideEstimateRepository,
    @DefaultHomeState initialState: HomeState,
    @DefaultHomeReducer reducer: HomeReducer,
    @IOScope private val ioScope: CoroutineScope
) : BasePresenter<HomeAction, HomeEvent, HomeState, HomeSideEffect>(
    initialState = initialState,
    actionProcessor = HomeActionProcessor(repository = repository, scope = ioScope),
    reducer = reducer,
    scope = ioScope
)