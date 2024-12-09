package com.bruno13palhano.data.di

import com.bruno13palhano.data.remote.datasource.TravelInfoRemote
import com.bruno13palhano.data.remote.datasource.TravelInfoRemoteImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
internal annotation class TravelInfoRemoteDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RemoteModule {

    @TravelInfoRemoteDataSource
    @Binds
    @Singleton
    abstract fun bindRemote(remote: TravelInfoRemoteImpl): TravelInfoRemote
}