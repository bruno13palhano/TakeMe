package com.bruno13palhano.data.remote.datasource

import com.bruno13palhano.data.model.ErrorResponse
import com.bruno13palhano.data.remote.model.DriverRequest
import com.bruno13palhano.data.remote.model.RideEstimateResponse
import com.bruno13palhano.data.remote.model.RemoteErrorResponse
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.asExternal
import com.bruno13palhano.data.remote.model.ConfirmRideRequest
import com.bruno13palhano.data.remote.model.ConfirmRideResponse
import com.bruno13palhano.data.remote.model.RidesResponse
import com.bruno13palhano.data.remote.service.Service
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import javax.inject.Inject

internal class RemoteDataSourceImpl @Inject constructor(
    private val service: Service
) : RemoteDataSource {
    override suspend fun searchDriver(driverRequest: DriverRequest): Resource<RideEstimateResponse> {
        val response = service.findDriver(request = driverRequest)

        return getResponse(response = response)
    }

    override suspend fun confirmRide(confirmRideRequest: ConfirmRideRequest): Resource<ConfirmRideResponse> {
        val response = service.confirmRide(request = confirmRideRequest)

        return getResponse(response = response)
    }

    override suspend fun getCustomerRides(
        customerId: String,
        driverId: Long
    ): Resource<RidesResponse> {
        val response = service.getCustomerRides(customerId = customerId, driverId = driverId)

        return getResponse(response = response)
    }

    override suspend fun getRides(customerId: String, driverId: Long): Resource<RidesResponse> {
        val response = service.getCustomerRides(customerId = customerId, driverId = driverId)

        return getResponse(response = response)
    }

    private fun <T> getResponse(response: Response<T>): Resource<T> {
        val result = response.body()

        return try {
            if (response.isSuccessful && result != null) {
                Resource.Success(data = result)
            } else {
                Resource.ServerResponseError(
                    errorResponse = getInvalidResponse(response = response.errorBody()!!.string())
                )
            }
        } catch (e: Exception) {
            Resource.Error(message = e.message.toString())
        }
    }

    private fun getInvalidResponse(response: String): ErrorResponse {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        return moshi.adapter(RemoteErrorResponse::class.java).fromJson(response)!!.asExternal()
    }
}