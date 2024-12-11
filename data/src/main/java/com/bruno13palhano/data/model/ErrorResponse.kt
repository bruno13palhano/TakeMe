package com.bruno13palhano.data.model

import com.bruno13palhano.data.remote.model.response.RemoteErrorResponse

data class ErrorResponse(
    val errorCode: String?,
    val errorDescription: String?
)

internal fun RemoteErrorResponse.asExternal() = ErrorResponse(
    errorCode = errorCode,
    errorDescription = errorDescription
)