package com.bruno13palhano.data.remote.model.request

import com.bruno13palhano.data.model.DriverInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ConfirmRideRequest(
    @Json(name = "customer_id") val customerId: String,
    @Json(name = "origin") val origin: String,
    @Json(name = "destination") val destination: String,
    @Json(name = "distance") val distance: Double,
    @Json(name = "duration") val duration: String,
    @Json(name = "driver") val driver: DriverInfo,
    @Json(name = "value") val value: Float
)