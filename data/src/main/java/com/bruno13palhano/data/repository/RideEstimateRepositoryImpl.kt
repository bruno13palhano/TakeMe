package com.bruno13palhano.data.repository

import com.bruno13palhano.data.di.Dispatcher
import com.bruno13palhano.data.di.TakeMeDispatchers.IO
import com.bruno13palhano.data.di.TravelInfoRemoteDataSource
import com.bruno13palhano.data.local.datasource.RideEstimateDao
import com.bruno13palhano.data.remote.datasource.RemoteDataSource
import com.bruno13palhano.data.remote.model.request.DriverRequest
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.data.model.asExternal
import com.bruno13palhano.data.model.asExternalResponse
import com.bruno13palhano.data.model.asInternal
import com.bruno13palhano.data.model.convert
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class RideEstimateRepositoryImpl @Inject constructor(
    private val rideEstimateData: RideEstimateDao,
    @TravelInfoRemoteDataSource private val remote: RemoteDataSource,
    @Dispatcher(IO) private val dispatcher: CoroutineDispatcher
) : RideEstimateRepository {
    override suspend fun insertRideEstimate(rideEstimate: RideEstimate) {
        rideEstimateData.insert(rideEstimate = rideEstimate.asInternal())
    }

    override suspend fun searchDriver(
        customerId: String?,
        origin: String?,
        destination: String?
    ): Resource<RideEstimate> = withContext(dispatcher) {
        val result = remote.searchDriver(
            driverRequest = DriverRequest(
                customerId = customerId,
                origin = origin,
                destination = destination
            )
        )

        result.convert(data = result.data?.asExternalResponse() ?: RideEstimate.empty)
    }

    override suspend fun getLastRideEstimate(): RideEstimate? {
        return rideEstimateData.getLastRideEstimate()?.asExternal()
    }
}