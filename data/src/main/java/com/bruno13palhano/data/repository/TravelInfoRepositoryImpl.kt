package com.bruno13palhano.data.repository

import com.bruno13palhano.data.di.RideEstimateLocalDataSource
import com.bruno13palhano.data.di.TravelInfoRemoteDataSource
import com.bruno13palhano.data.local.datasource.RideEstimateLocal
import com.bruno13palhano.data.local.model.RideEstimateEntity
import com.bruno13palhano.data.remote.datasource.TravelInfoRemote
import com.bruno13palhano.data.remote.model.RequestDriver
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.data.model.asExternal
import com.bruno13palhano.data.model.asExternalResponse
import com.bruno13palhano.data.model.asInternal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TravelInfoRepositoryImpl @Inject constructor(
    @RideEstimateLocalDataSource private val rideEstimateData: RideEstimateLocal<RideEstimateEntity>,
    @TravelInfoRemoteDataSource private val remote: TravelInfoRemote
) : TravelInfoRepository {
    override suspend fun insertRideEstimate(rideEstimate: RideEstimate) {
        rideEstimateData.insert(
            rideEstimate = rideEstimate.asInternal()
        )
    }

    override suspend fun searchDriver(
        customerId: String?,
        origin: String?,
        destination: String?
    ): Resource<RideEstimate> {
        val result =  remote.searchDriver(
            requestDriver = RequestDriver(
                customerId = customerId,
                origin = origin,
                destination = destination
            )
        )

        return when (result) {
            is Resource.Success -> {
                if (result.data != null) {
                    Resource.Success(data = result.data.asExternalResponse())
                } else {
                    Resource.Success(data = RideEstimate.empty)
                }
            }

            is Resource.Error -> {
                Resource.Error(message = result.message ?: "")
            }

            is Resource.ServerResponseError -> {
                Resource.ServerResponseError(errorResponse = result.remoteErrorResponse)
            }
        }
    }

    override fun getLastRideEstimate(): Flow<RideEstimate?> {
        return rideEstimateData.getLastRideEstimate().map { it?.asExternal() }
    }
}