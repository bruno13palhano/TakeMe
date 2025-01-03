package com.bruno13palhano.data.remote.service

import com.bruno13palhano.data.remote.model.request.ConfirmRideRequest
import com.bruno13palhano.data.remote.model.response.ConfirmRideResponse
import com.bruno13palhano.data.remote.model.response.RideEstimateResponse
import com.bruno13palhano.data.remote.model.request.DriverRequest
import com.bruno13palhano.data.remote.model.response.RidesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface Service {
    @POST("ride/estimate")
    suspend fun findDriver(
        @Body request: DriverRequest
    ): Response<RideEstimateResponse>

    @PATCH("ride/confirm")
    suspend fun confirmRide(
        @Body request: ConfirmRideRequest
    ): Response<ConfirmRideResponse>

    @GET("ride/{customer_id}?driver_id")
    suspend fun getCustomerRides(
        @Path("customer_id") customerId: String,
        @Query("driver_id") driverId: Long
    ): Response<RidesResponse>
}