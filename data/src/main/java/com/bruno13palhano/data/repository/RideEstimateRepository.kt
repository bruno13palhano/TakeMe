package com.bruno13palhano.data.repository

import com.bruno13palhano.data.model.ConfirmRide
import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.model.RequestConfirmRide
import com.bruno13palhano.data.model.Resource
import com.bruno13palhano.data.model.Ride
import com.bruno13palhano.data.model.RideEstimate
import kotlinx.coroutines.flow.Flow

interface TravelInfoRepository {
    suspend fun insertRideEstimate(rideEstimate: RideEstimate)

    suspend fun searchDriver(
        customerId: String?,
        origin: String?,
        destination: String?
    ): Resource<RideEstimate>

    suspend fun confirmRide(confirmRide: RequestConfirmRide): Resource<ConfirmRide>

    suspend fun getCustomerRides(
        customerId: String,
        driverId: Long,
        driverName: String
    ): Resource<List<Ride>>

    fun getLastRideEstimate(): Flow<RideEstimate?>

    suspend fun insertDriverInfo(driverInfo: DriverInfo)

    fun getAllDriverInfo(): Flow<List<DriverInfo>>
}