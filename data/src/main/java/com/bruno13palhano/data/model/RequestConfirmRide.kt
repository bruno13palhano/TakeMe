package com.bruno13palhano.data.model

import com.bruno13palhano.data.remote.model.ConfirmRideRequest

data class RequestConfirmRide(
    val customerId: String,
    val origin: String,
    val destination: String,
    val distance: Double,
    val duration: String,
    val driverId: Long,
    val driverName: String,
    val value: Float
)

internal fun RequestConfirmRide.asInternalRequest() = ConfirmRideRequest(
    customerId = customerId,
    origin = origin,
    destination = destination,
    distance = distance,
    duration = duration,
    driver = DriverInfo(id = driverId, name = driverName),
    value = value
)