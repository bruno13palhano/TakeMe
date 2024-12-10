package com.bruno13palhano.data.repository

import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.Ride

interface RidesRepository {
    suspend fun getCustomerRides(
        customerId: String,
        driverId: Long,
        driverName: String
    ): Resource<List<Ride>>
}