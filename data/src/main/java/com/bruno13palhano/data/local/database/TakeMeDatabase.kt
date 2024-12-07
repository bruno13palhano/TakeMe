package com.bruno13palhano.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bruno13palhano.data.local.datasource.RideEstimateDao
import com.bruno13palhano.data.local.model.RideEstimateEntity

@Database(
    entities = [
        RideEstimateEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    value = [
        CoordinatesConverter::class,
        DriverListConverter::class
    ]
)
internal abstract class TakeMeDatabase : RoomDatabase() {
    abstract val rideEstimateDao: RideEstimateDao
}