package com.bruno13palhano.data.remote.datasource

import com.bruno13palhano.data.remote.model.RequestDriver
import com.bruno13palhano.data.remote.model.RideEstimateResponse
import com.bruno13palhano.data.model.Resource

internal interface TravelInfoRemote {
    suspend fun searchDriver(requestDriver: RequestDriver): Resource<RideEstimateResponse>
}