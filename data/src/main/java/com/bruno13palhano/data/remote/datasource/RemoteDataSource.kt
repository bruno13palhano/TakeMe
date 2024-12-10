package com.bruno13palhano.data.remote.datasource

import com.bruno13palhano.data.remote.model.DriverRequest
import com.bruno13palhano.data.remote.model.RideEstimateResponse
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.remote.model.ConfirmRideRequest
import com.bruno13palhano.data.remote.model.ConfirmRideResponse
import com.bruno13palhano.data.remote.model.RidesResponse

internal interface RemoteDataSource {
    suspend fun searchDriver(driverRequest: DriverRequest): Resource<RideEstimateResponse>

    suspend fun confirmRide(confirmRideRequest: ConfirmRideRequest): Resource<ConfirmRideResponse>

    suspend fun getCustomerRides(customerId: String, driverId: Long): Resource<RidesResponse>

    suspend fun getRides(customerId: String, driverId: Long): Resource<RidesResponse>
}