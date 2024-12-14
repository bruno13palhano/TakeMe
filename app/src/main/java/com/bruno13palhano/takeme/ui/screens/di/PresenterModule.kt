package com.bruno13palhano.takeme.ui.screens.di

import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerReducer
import com.bruno13palhano.takeme.ui.screens.driverpicker.presenter.DriverPickerState
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeReducer
import com.bruno13palhano.takeme.ui.screens.home.presenter.HomeState
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryReducer
import com.bruno13palhano.takeme.ui.screens.travelhistory.presenter.TravelHistoryState

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class DefaultHomeState

@Qualifier
annotation class DefaultHomeReducer

@Qualifier
annotation class DefaultDriverPickerState

@Qualifier
annotation class DefaultDriverPickerReducer

@Qualifier
annotation class DefaultTravelHistoryState

@Qualifier
annotation class DefaultTravelHistoryReducer

@Module
@InstallIn(SingletonComponent::class)
internal object PresenterModule {
    @DefaultHomeState
    @Provides
    @Singleton
    fun provideHomeState(): HomeState = HomeState.initialState

    @DefaultHomeReducer
    @Provides
    @Singleton
    fun provideHomeReducer(): HomeReducer = HomeReducer()

    @DefaultDriverPickerState
    @Provides
    @Singleton
    fun provideDriverPickerState(): DriverPickerState = DriverPickerState.initialState

    @DefaultDriverPickerReducer
    @Provides
    @Singleton
    fun provideDriverPickerReducer(): DriverPickerReducer = DriverPickerReducer()

    @DefaultTravelHistoryState
    @Provides
    @Singleton
    fun provideTravelHistoryState(): TravelHistoryState = TravelHistoryState.initialState

    @DefaultTravelHistoryReducer
    @Provides
    @Singleton
    fun provideTravelHistoryReducer(): TravelHistoryReducer = TravelHistoryReducer()
}