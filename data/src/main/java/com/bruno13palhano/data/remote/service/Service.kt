package com.bruno13palhano.data.remote.service

import com.bruno13palhano.data.remote.model.RideEstimateResponse
import com.bruno13palhano.data.remote.model.RequestDriver
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

internal interface Service {
    @POST("ride/estimate")
    suspend fun findDriver(
        @Body request: RequestDriver
    ): Response<RideEstimateResponse>
}