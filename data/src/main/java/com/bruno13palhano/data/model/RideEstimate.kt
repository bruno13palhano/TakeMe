package com.bruno13palhano.data.model

import com.bruno13palhano.data.local.model.RideEstimateEntity
import com.bruno13palhano.data.remote.model.RideEstimateResponse

data class RideEstimate(
    val origin: Coordinates,
    val destination: Coordinates,
    val distance: Double,
    val duration: String,
    val drivers: List<Driver>
) {
    companion object {
        val empty = RideEstimate(
            origin = Coordinates.empty,
            destination = Coordinates.empty,
            distance = 0.0,
            duration = "",
            drivers = emptyList()
        )
    }
}

internal fun RideEstimate.asInternal() = RideEstimateEntity(
    id = 1L,
    origin = origin,
    destination = destination,
    distance = distance,
    duration = duration,
    drivers = drivers
)

internal fun RideEstimateEntity.asExternal() = RideEstimate(
    origin = origin ?: Coordinates.empty,
    destination = destination ?: Coordinates.empty,
    distance = distance ?: 0.0,
    duration = duration ?: "",
    drivers = drivers ?: emptyList()
)

internal fun RideEstimateResponse.asExternalResponse() = RideEstimate(
    origin = origin ?: Coordinates.empty,
    destination = destination ?: Coordinates.empty,
    distance = distance ?: 0.0,
    duration = duration ?: "",
    drivers = drivers ?: emptyList()
)