package com.bruno13palhano.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class IOScope

@Module
@InstallIn(SingletonComponent::class)
internal object CoroutineScopeModule {
    @Singleton
    @IOScope
    @Provides
    fun providesIOCoroutineScope(
        @Dispatcher(TakeMeDispatchers.IO) dispatcher: CoroutineDispatcher
    ): CoroutineScope = CoroutineScope(SupervisorJob() + dispatcher)
}