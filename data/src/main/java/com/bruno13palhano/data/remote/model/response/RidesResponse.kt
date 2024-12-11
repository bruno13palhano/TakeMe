package com.bruno13palhano.data.remote.model.response

import com.bruno13palhano.data.model.Ride
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RidesResponse(
    @Json(name = "customer_id") val customerId: String,
    @Json(name = "rides") val rides: List<Ride>
)