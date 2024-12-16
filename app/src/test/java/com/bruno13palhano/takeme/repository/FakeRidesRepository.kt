package com.bruno13palhano.takeme.repository

import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.model.ErrorResponse
import com.bruno13palhano.data.model.InternalError
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.Ride
import com.bruno13palhano.data.repository.RidesRepository

internal class FakeRidesRepository : RidesRepository {
    private val rides = listOf(
        Ride(
            id = 1L,
            date = "1",
            origin = "origin 1",
            destination = "destination 1",
            distance = 1000.0,
            duration = "1000s",
            driver = DriverInfo(1L, "Driver 1", 1.0f),
            value = 200f
        ),
        Ride(
            id = 2L,
            date = "2",
            origin = "origin 2",
            destination = "destination 2",
            distance = 2000.0,
            duration = "2000s",
            driver = DriverInfo(2L, "Driver 2", 2.0f),
            value = 400f
        ),
        Ride(
            id = 3L,
            date = "3",
            origin = "origin 3",
            destination = "destination 3",
            distance = 3000.0,
            duration = "3000s",
            driver = DriverInfo(3L, "Driver 3", 3.0f),
            value = 600f
        ),
        Ride(
            id = 4L,
            date = "11",
            origin = "origin 11",
            destination = "destination 11",
            distance = 1100.0,
            duration = "1100s",
            driver = DriverInfo(1L, "Driver 1", 1.0f),
            value = 230f
        ),
    )

    override suspend fun getCustomerRides(
        customerId: String,
        driverId: Long,
        driverName: String
    ): Resource<List<Ride>> {
        return when (customerId) {
            "ServerErrorResponse" -> {
                Resource.ServerResponseError(
                    errorResponse = ErrorResponse("401", "Unauthorized")
                )
            }

            "InternalError" -> {
                Resource.Error(InternalError.UNKNOWN_ERROR)
            }

            else -> Resource.Success(rides.filter { it.driver.id == driverId })
        }
    }
}
