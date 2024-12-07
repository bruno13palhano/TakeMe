package com.bruno13palhano.data.local.datasource

import kotlinx.coroutines.flow.Flow

internal interface RideEstimateLocal<T> {
    suspend fun insert(rideEstimate: T)

    fun getLastRideEstimate(): Flow<T?>
}