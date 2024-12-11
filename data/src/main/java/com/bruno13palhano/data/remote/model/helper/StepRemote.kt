package com.bruno13palhano.data.remote.model.helper

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
internal data class StepRemote(
    @Json(name = "startLocation") val startLocation: LocationRemote,
    @Json(name = "endLocation") val endLocation: LocationRemote
)