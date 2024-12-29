package com.bruno13palhano.data.repository

import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.RideEstimate

interface RideEstimateRepository {
    suspend fun insertRideEstimate(rideEstimate: RideEstimate)

    suspend fun searchDriver(
        customerId: String?,
        origin: String?,
        destination: String?
    ): Resource<RideEstimate>

    suspend fun getLastRideEstimate(): RideEstimate?
}