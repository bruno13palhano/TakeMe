package com.bruno13palhano.data.local.datasource

internal interface RideEstimateLocal<T> {
    suspend fun insert(rideEstimate: T)

    suspend fun getLastRideEstimate(): T?
}