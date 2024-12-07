package com.bruno13palhano.data.model

sealed class Resource<T>(
    val data: T?,
    val message: String?,
    val remoteErrorResponse: ErrorResponse?
) {
    class Success<T>(data: T) : Resource<T>(data, null, null)
    class Error<T>(message: String) : Resource<T>(null, message, null)
    class ServerResponseError<T>(errorResponse: ErrorResponse?) : Resource<T>(
        null,
        null,
        errorResponse
    )
}