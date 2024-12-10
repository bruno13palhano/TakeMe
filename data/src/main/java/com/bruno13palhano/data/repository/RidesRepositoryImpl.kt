package com.bruno13palhano.data.repository

import com.bruno13palhano.data.di.TravelInfoRemoteDataSource
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.Ride
import com.bruno13palhano.data.model.convert
import com.bruno13palhano.data.remote.datasource.RemoteDataSource
import javax.inject.Inject

internal class RidesRepositoryImpl @Inject constructor(
    @TravelInfoRemoteDataSource private val remote: RemoteDataSource
) : RidesRepository {
    override suspend fun getCustomerRides(
        customerId: String,
        driverId: Long,
        driverName: String
    ): Resource<List<Ride>> {
        val result = remote.getCustomerRides(
            customerId = customerId,
            driverId = driverId
        )

        val rides = result.data?.rides ?: emptyList()

        return result.convert(data = filterRidesByDriverId(rides = rides, driverName = driverName))
    }

    private fun filterRidesByDriverId(rides: List<Ride>, driverName: String): List<Ride> {
        return rides.filter { it.driver.name == driverName }
    }
}