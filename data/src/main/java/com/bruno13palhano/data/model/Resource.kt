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

/**
 * Convert a internal [Resource] to external [Resource] and vice versa.
 *
 *
 * @param data The data in the proper format
 *
 * @return The converted [Resource]
 */
internal fun <T, R> Resource<T>.convert(data: R): Resource<R> {
    return when (this) {
        is Resource.Success -> { Resource.Success(data = data) }

        is Resource.Error -> { Resource.Error(message = this.message ?: "") }

        is Resource.ServerResponseError -> {
            Resource.ServerResponseError(errorResponse = this.remoteErrorResponse)
        }
    }
}