package com.bruno13palhano.data.local.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bruno13palhano.data.local.model.RideEstimateEntity

@Dao
internal interface RideEstimateDao : RideEstimateLocal<RideEstimateEntity> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override suspend fun insert(rideEstimate: RideEstimateEntity)

    @Query("SELECT * FROM ride_estimates ORDER BY id DESC LIMIT 1")
    override suspend fun getLastRideEstimate(): RideEstimateEntity?
}