package com.bruno13palhano.data.remote.model.response

import com.bruno13palhano.data.remote.model.helper.RoutesRemote
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RouteResponse(
    @Json(name = "routes") val routes: List<RoutesRemote>?
)