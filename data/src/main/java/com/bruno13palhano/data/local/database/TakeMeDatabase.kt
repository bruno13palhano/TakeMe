package com.bruno13palhano.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bruno13palhano.data.local.datasource.DriverInfoDao
import com.bruno13palhano.data.local.datasource.RideEstimateDao
import com.bruno13palhano.data.local.model.DriverInfoEntity
import com.bruno13palhano.data.local.model.RideEstimateEntity

@Database(
    entities = [
        RideEstimateEntity::class,
        DriverInfoEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    value = [
        DriverListConverter::class,
        RouteConverter::class
    ]
)
internal abstract class TakeMeDatabase : RoomDatabase() {
    abstract val rideEstimateDao: RideEstimateDao
    abstract val driverInfoDao: DriverInfoDao
}