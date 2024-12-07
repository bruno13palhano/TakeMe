package com.bruno13palhano.data.remote.datasource

import com.bruno13palhano.data.model.ErrorResponse
import com.bruno13palhano.data.remote.model.RequestDriver
import com.bruno13palhano.data.remote.model.RideEstimateResponse
import com.bruno13palhano.data.remote.model.RemoteErrorResponse
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.asExternal
import com.bruno13palhano.data.remote.service.Service
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import javax.inject.Inject

internal class TravelInfoRemoteImpl @Inject constructor(
    private val service: Service
) : TravelInfoRemote {
    override suspend fun searchDriver(requestDriver: RequestDriver): Resource<RideEstimateResponse> {
        val response = service.findDriver(request = requestDriver)

        val result = response.body()

        return try {
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.ServerResponseError(getInvalidResponse(response.errorBody()!!.string()))
            }
        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    private fun getInvalidResponse(response: String): ErrorResponse {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        return moshi.adapter(RemoteErrorResponse::class.java).fromJson(response)!!.asExternal()
    }
}