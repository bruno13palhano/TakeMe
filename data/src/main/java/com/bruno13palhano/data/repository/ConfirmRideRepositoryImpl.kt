package com.bruno13palhano.data.repository

import com.bruno13palhano.data.di.Dispatcher
import com.bruno13palhano.data.di.TakeMeDispatchers
import com.bruno13palhano.data.di.TravelInfoRemoteDataSource
import com.bruno13palhano.data.local.datasource.DriverInfoDao
import com.bruno13palhano.data.model.ConfirmRide
import com.bruno13palhano.data.model.ErrorResponse
import com.bruno13palhano.data.model.RequestConfirmRide
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.asExternalResponse
import com.bruno13palhano.data.model.asInternalRequest
import com.bruno13palhano.data.model.convert
import com.bruno13palhano.data.remote.datasource.RemoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ConfirmRideRepositoryImpl @Inject constructor(
    private val driverInfoLocal: DriverInfoDao,
    @TravelInfoRemoteDataSource private val remote: RemoteDataSource,
    @Dispatcher(TakeMeDispatchers.IO) private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ConfirmRideRepository {
    override suspend fun confirmRide(confirmRide: RequestConfirmRide): Resource<ConfirmRide> {
        val driverInfo = driverInfoLocal.getDriverInfo(id = confirmRide.driverId)

        driverInfo?.minKm?.let {
            if (it > distanceInMetersToKm(confirmRide.distance)) {
                return Resource.ServerResponseError(
                    errorResponse = ErrorResponse(
                        errorCode = "INVALID_DISTANCE",
                        errorDescription = "Invalid distance"
                    )
                )
            }
        }

        return withContext(dispatcher) {
            val result = remote.confirmRide(
                confirmRideRequest = confirmRide.asInternalRequest()
            )

            result.convert(data = result.data?.asExternalResponse() ?: ConfirmRide.empty)
        }
    }

    private fun distanceInMetersToKm(distanceInKm: Double): Double {
        return distanceInKm / 1000
    }
}