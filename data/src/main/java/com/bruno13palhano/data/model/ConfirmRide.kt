package com.bruno13palhano.data.model

import com.bruno13palhano.data.remote.model.ConfirmRideResponse

data class ConfirmRide(
    val success: Boolean
) {
    companion object {
        val empty = ConfirmRide(
            success = false
        )
    }
}

internal fun ConfirmRideResponse.asExternalResponse() = ConfirmRide(
    success = success
)
