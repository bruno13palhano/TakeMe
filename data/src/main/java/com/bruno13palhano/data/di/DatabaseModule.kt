package com.bruno13palhano.data.di

import android.content.Context
import androidx.room.Room
import com.bruno13palhano.data.local.database.TakeMeDatabase
import com.bruno13palhano.data.local.datasource.RideEstimateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideRideEstimateDao(database: TakeMeDatabase): RideEstimateDao = database.rideEstimateDao

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): TakeMeDatabase {
        return Room.databaseBuilder(
            context,
            TakeMeDatabase::class.java,
            "take_me_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}