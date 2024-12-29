package com.bruno13palhano.takeme.ui.screens.home.viewmodel

import com.bruno13palhano.data.di.RideEstimateRep
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.takeme.ui.screens.di.DefaultHomeReducer
import com.bruno13palhano.takeme.ui.screens.di.DefaultHomeState
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeAction
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeActionProcessor
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeEvent
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeReducer
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeSideEffect
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeState
import com.bruno13palhano.takeme.ui.shared.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    @RideEstimateRep private val repository: RideEstimateRepository,
    @DefaultHomeState private val initialHomeState: HomeState,
    @DefaultHomeReducer private val homeReducer: HomeReducer
) : BaseViewModel<HomeState, HomeAction, HomeEvent, HomeSideEffect>(
    initialState = initialHomeState,
    actionProcessor = HomeActionProcessor(repository = repository),
    reducer = homeReducer
)