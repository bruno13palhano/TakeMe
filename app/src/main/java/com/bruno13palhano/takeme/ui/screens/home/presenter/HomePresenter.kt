package com.bruno13palhano.takeme.ui.screens.home.presenter

import com.bruno13palhano.data.di.RideEstimateRep
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.di.DefaultHomeReducer
import com.bruno13palhano.takeme.ui.screens.di.DefaultHomeState
import com.bruno13palhano.takeme.ui.shared.base.BasePresenter
import javax.inject.Inject

internal class HomePresenter @Inject constructor(
    @RideEstimateRep private val repository: RideEstimateRepository,
    @DefaultHomeState initialState: HomeState,
    @DefaultHomeReducer reducer: HomeReducer,
) : BasePresenter<HomeAction, HomeEvent, HomeState, HomeSideEffect>(
    initialState = initialState,
    actionProcessor = HomeActionProcessor(repository = repository),
    reducer = reducer
)