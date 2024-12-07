package com.bruno13palhano.data.di

import com.bruno13palhano.data.repository.TravelInfoRepository
import com.bruno13palhano.data.repository.TravelInfoRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class TravelInfoRep

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @TravelInfoRep
    @Binds
    @Singleton
    abstract fun provideRepository(repository: TravelInfoRepositoryImpl): TravelInfoRepository
}