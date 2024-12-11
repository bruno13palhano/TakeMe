package com.bruno13palhano.data.repository

import com.bruno13palhano.data.di.RideEstimateLocalDataSource
import com.bruno13palhano.data.di.TravelInfoRemoteDataSource
import com.bruno13palhano.data.local.datasource.RideEstimateLocal
import com.bruno13palhano.data.local.model.RideEstimateEntity
import com.bruno13palhano.data.remote.datasource.RemoteDataSource
import com.bruno13palhano.data.remote.model.request.DriverRequest
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.data.model.asExternal
import com.bruno13palhano.data.model.asExternalResponse
import com.bruno13palhano.data.model.asInternal
import com.bruno13palhano.data.model.convert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class RideEstimateRepositoryImpl @Inject constructor(
    @RideEstimateLocalDataSource private val rideEstimateData: RideEstimateLocal<RideEstimateEntity>,
    @TravelInfoRemoteDataSource private val remote: RemoteDataSource
) : RideEstimateRepository {
    override suspend fun insertRideEstimate(rideEstimate: RideEstimate) {
        rideEstimateData.insert(rideEstimate = rideEstimate.asInternal())
    }

    override suspend fun searchDriver(
        customerId: String?,
        origin: String?,
        destination: String?
    ): Resource<RideEstimate> {
        val result =  remote.searchDriver(
            driverRequest = DriverRequest(
                customerId = customerId,
                origin = origin,
                destination = destination
            )
        )

        return result.convert(data = result.data?.asExternalResponse() ?: RideEstimate.empty)
    }

    override fun getLastRideEstimate(): Flow<RideEstimate?> {
        return rideEstimateData.getLastRideEstimate().map { it?.asExternal() }
    }
}