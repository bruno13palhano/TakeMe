package com.bruno13palhano.takeme.repository

import com.bruno13palhano.data.model.ErrorResponse
import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.RideEstimate
import com.bruno13palhano.data.repository.RideEstimateRepository

internal class FakeRideEstimateRepository : RideEstimateRepository {
    private val rides = mutableListOf<RideEstimate>()

    override suspend fun insertRideEstimate(rideEstimate: RideEstimate) {
        rides.add(rideEstimate)
    }

    override suspend fun searchDriver(
        customerId: String?,
        origin: String?,
        destination: String?
    ): Resource<RideEstimate> {
        val randomIndex = (rides.indices).random()

        return when (customerId) {
            "ServerErrorResponse" -> {
                Resource.ServerResponseError(
                    errorResponse = ErrorResponse("401", "Unauthorized")
                )
            }

            "InternalError" -> {
                Resource.Error(internalError = InternalError.UNKNOWN_ERROR)
            }

            else -> Resource.Success(rides[randomIndex])
        }
    }

    override suspend fun getLastRideEstimate(): RideEstimate? {
        return rides.lastOrNull()
    }
}