package com.bruno13palhano.data.remote.model

import com.bruno13palhano.data.model.Coordinates
import com.bruno13palhano.data.model.Driver
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RideEstimateResponse(
    @Json(name = "origin") val origin: Coordinates?,
    @Json(name = "destination") val destination: Coordinates?,
    @Json(name = "distance") val distance: Double?,
    @Json(name = "duration") val duration: String?,
    @Json(name = "options") val drivers: List<Driver>?
)