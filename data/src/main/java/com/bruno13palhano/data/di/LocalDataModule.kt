package com.bruno13palhano.data.di

import com.bruno13palhano.data.local.datasource.RideEstimateDao
import com.bruno13palhano.data.local.datasource.RideEstimateLocal
import com.bruno13palhano.data.local.model.RideEstimateEntity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
internal annotation class RideEstimateLocalDataSource

@Module
@InstallIn(SingletonComponent::class)
internal abstract class LocalDataModule {

    @RideEstimateLocalDataSource
    @Binds
    @Singleton
    abstract fun bindLocal(local: RideEstimateDao): RideEstimateLocal<RideEstimateEntity>
}