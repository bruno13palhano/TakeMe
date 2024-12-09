package com.bruno13palhano.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ConfirmRideResponse(
    @Json(name = "success") val success: Boolean
)