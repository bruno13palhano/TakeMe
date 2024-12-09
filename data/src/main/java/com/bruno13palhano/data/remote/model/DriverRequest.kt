package com.bruno13palhano.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class DriverRequest(
    @Json(name = "customer_id") val customerId: String?,
    @Json(name = "origin") val origin: String?,
    @Json(name = "destination") val destination: String?
)