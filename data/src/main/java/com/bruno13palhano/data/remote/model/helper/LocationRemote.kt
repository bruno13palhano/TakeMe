package com.bruno13palhano.data.remote.model.helper

import com.bruno13palhano.data.model.Coordinates
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class LocationRemote(
    @Json(name= "latLng") val location: Coordinates
)