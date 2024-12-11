package com.bruno13palhano.data.remote.datasource

import com.bruno13palhano.data.remote.model.request.DriverRequest
import com.bruno13palhano.data.remote.model.response.RideEstimateResponse
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.remote.model.request.ConfirmRideRequest
import com.bruno13palhano.data.remote.model.response.ConfirmRideResponse
import com.bruno13palhano.data.remote.model.response.RidesResponse

internal interface RemoteDataSource {
    suspend fun searchDriver(driverRequest: DriverRequest): Resource<RideEstimateResponse>

    suspend fun confirmRide(confirmRideRequest: ConfirmRideRequest): Resource<ConfirmRideResponse>

    suspend fun getCustomerRides(customerId: String, driverId: Long): Resource<RidesResponse>

    suspend fun getRides(customerId: String, driverId: Long): Resource<RidesResponse>
}