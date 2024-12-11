package com.bruno13palhano.data.remote.model.helper

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class LegRemote(
    @Json(name = "steps") val steps: List<StepRemote>?
)
