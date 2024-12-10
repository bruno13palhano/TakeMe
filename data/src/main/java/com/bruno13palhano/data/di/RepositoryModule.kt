package com.bruno13palhano.data.di

import com.bruno13palhano.data.repository.ConfirmRideRepository
import com.bruno13palhano.data.repository.ConfirmRideRepositoryImpl
import com.bruno13palhano.data.repository.DriverInfoRepository
import com.bruno13palhano.data.repository.DriverInfoRepositoryImpl
import com.bruno13palhano.data.repository.RideEstimateRepository
import com.bruno13palhano.data.repository.RideEstimateRepositoryImpl
import com.bruno13palhano.data.repository.RidesRepository
import com.bruno13palhano.data.repository.RidesRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class ConfirmRideRep

@Qualifier
annotation class DriverInfoRep

@Qualifier
annotation class RideEstimateRep

@Qualifier
annotation class RidesRep

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @ConfirmRideRep
    @Binds
    @Singleton
    abstract fun bindConfirmRideRepository(repository: ConfirmRideRepositoryImpl): ConfirmRideRepository

    @DriverInfoRep
    @Binds
    @Singleton
    abstract fun bindDriverInfoRepository(repository: DriverInfoRepositoryImpl): DriverInfoRepository

    @RideEstimateRep
    @Binds
    @Singleton
    abstract fun bindRideEstimateRepository(repository: RideEstimateRepositoryImpl): RideEstimateRepository

    @RidesRep
    @Binds
    @Singleton
    abstract fun bindRidesRepository(repository: RidesRepositoryImpl): RidesRepository
}