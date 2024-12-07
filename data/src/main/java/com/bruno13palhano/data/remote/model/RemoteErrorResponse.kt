package com.bruno13palhano.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class RemoteErrorResponse(
    @Json(name = "error_code") val errorCode: String?,
    @Json(name = "error_description") val errorDescription: String?
)
