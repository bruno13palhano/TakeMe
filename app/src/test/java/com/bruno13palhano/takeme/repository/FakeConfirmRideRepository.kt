package com.bruno13palhano.takeme.repository

import com.bruno13palhano.data.model.ConfirmRide
import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.model.ErrorResponse
import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.data.model.RequestConfirmRide
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.repository.ConfirmRideRepository

internal class FakeConfirmRideRepository : ConfirmRideRepository {
    private val driversInfo = mutableListOf(
        DriverInfo(1, "Driver 1", 1f),
        DriverInfo(2, "Driver 2", 5f),
        DriverInfo(3, "Driver 3", 10f),
    )

    override suspend fun confirmRide(confirmRide: RequestConfirmRide): Resource<ConfirmRide> {
        val driverInfo = driversInfo.find { it.id == confirmRide.driverId }

        if (confirmRide.driverName == "NoDriverFound") {
            return Resource.ServerResponseError(
                errorResponse = ErrorResponse(
                    errorCode = "NO_DRIVER_FOUND",
                    errorDescription = "No driver found"
                )
            )
        } else if (confirmRide.driverName == "InternalError") {
            return Resource.Error(internalError = InternalError.UNKNOWN_ERROR)
        }

        driverInfo?.let {
            it.minKm?.let { minKm ->
                if (minKm > distanceInMetersToKm(confirmRide.distance)) {
                    return Resource.ServerResponseError(
                        errorResponse = ErrorResponse(
                            errorCode = "INVALID_DISTANCE",
                            errorDescription = "Invalid distance"
                        )
                    )
                } else {
                    return Resource.Success(ConfirmRide(success = true))
                }
            }
        }

        return Resource.ServerResponseError(
            errorResponse = ErrorResponse("500", "Internal Server Error")
        )
    }

    private fun distanceInMetersToKm(distanceInKm: Double): Double {
        return distanceInKm / 1000
    }
}