package com.bruno13palhano.takeme.ui.screens.home.viewmodel

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

): BaseViewModel<HomeState, HomeAction, HomeEvent, HomeSideEffect>(
    initialState = HomeState.initialState,
    actionProcessor = HomeActionProcessor(),
    reducer = HomeReducer()
) {

}