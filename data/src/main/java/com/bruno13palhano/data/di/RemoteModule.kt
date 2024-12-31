package com.bruno13palhano.data.di

import com.bruno13palhano.data.remote.datasource.RemoteDataSource
import com.bruno13palhano.data.remote.service.Service
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
internal annotation class TravelInfoRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
internal object RemoteModule {
    @TravelInfoRemoteDataSource
    @Provides
    @Singleton
    fun provideRemote(service: Service) = RemoteDataSource(service = service)
}