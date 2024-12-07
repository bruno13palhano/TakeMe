package com.bruno13palhano.data.di

import com.bruno13palhano.data.remote.service.Service
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

private const val BASE_URL = ""

@Module
@InstallIn(SingletonComponent::class)
internal object ServiceModule {
    @Provides
    @Singleton
    fun provideService(): Service {
        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val client =
            OkHttpClient.Builder()
                .build()
        val retrofit =
            Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .baseUrl(BASE_URL)
                .client(client)
                .build()

        val apiService: Service by lazy { retrofit.create(Service::class.java) }

        return apiService
    }
}