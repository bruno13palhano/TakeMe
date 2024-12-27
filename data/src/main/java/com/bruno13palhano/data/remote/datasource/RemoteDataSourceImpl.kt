package com.bruno13palhano.data.remote.datasource

import com.bruno13palhano.data.model.ErrorResponse
import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.data.remote.model.request.DriverRequest
import com.bruno13palhano.data.remote.model.response.RideEstimateResponse
import com.bruno13palhano.data.remote.model.response.RemoteErrorResponse
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.asExternal
import com.bruno13palhano.data.remote.model.request.ConfirmRideRequest
import com.bruno13palhano.data.remote.model.response.ConfirmRideResponse
import com.bruno13palhano.data.remote.model.response.RidesResponse
import com.bruno13palhano.data.remote.service.Service
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okio.IOException
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

internal class RemoteDataSourceImpl @Inject constructor(
    private val service: Service
) : RemoteDataSource {
    override suspend fun searchDriver(
        driverRequest: DriverRequest
    ): Resource<RideEstimateResponse> {
        val response = try {
            service.findDriver(request = driverRequest)
        } catch (e: HttpException) {
            return Resource.Error(internalError = InternalError.SERVER_ERROR)
        } catch (e: IOException) {
            return Resource.Error(internalError = InternalError.NO_INTERNET_CONNECTION)
        } catch (e: Exception) {
            return Resource.Error(internalError = InternalError.UNKNOWN_ERROR)
        }

        return getResponse(response = response)
    }

    override suspend fun confirmRide(
        confirmRideRequest: ConfirmRideRequest
    ): Resource<ConfirmRideResponse> {
        return handleError {
            val response = service.confirmRide(request = confirmRideRequest)
            getResponse(response = response)
        }
    }

    override suspend fun getCustomerRides(
        customerId: String,
        driverId: Long
    ): Resource<RidesResponse> {
        return handleError {
            val response = service.getCustomerRides(customerId = customerId, driverId = driverId)
            getResponse(response = response)
        }
    }

    override suspend fun getRides(customerId: String, driverId: Long): Resource<RidesResponse> {
        return handleError {
            val response = service.getCustomerRides(customerId = customerId, driverId = driverId)
            getResponse(response = response)
        }
    }

    private suspend fun <T> handleError(
        response: suspend () -> Resource<T>
    ): Resource<T> {
        return try {
            response()
        } catch (e: HttpException) {
            Resource.Error(internalError = InternalError.SERVER_ERROR)
        } catch (e: IOException) {
            Resource.Error(internalError = InternalError.NO_INTERNET_CONNECTION)
        } catch (e: Exception) {
            Resource.Error(internalError = InternalError.UNKNOWN_ERROR)
        }
    }

    private fun <T> getResponse(response: Response<T>): Resource<T> {
        val result = response.body()

        return if (response.isSuccessful && result != null) {
            Resource.Success(data = result)
        } else {
            Resource.ServerResponseError(
                errorResponse = getInvalidResponse(response = response.errorBody()!!.string())
            )
        }
    }

    private fun getInvalidResponse(response: String): ErrorResponse {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        return moshi.adapter(RemoteErrorResponse::class.java).fromJson(response)!!.asExternal()
    }
}