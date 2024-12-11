package com.bruno13palhano.data.model

import com.bruno13palhano.data.local.model.RideEstimateEntity
import com.bruno13palhano.data.remote.model.response.RideEstimateResponse

data class RideEstimate(
    val origin: Coordinates,
    val destination: Coordinates,
    val distance: Double,
    val duration: String,
    val drivers: List<Driver>,
    val route: Route
) {
    companion object {
        val empty = RideEstimate(
            origin = Coordinates.empty,
            destination = Coordinates.empty,
            distance = 0.0,
            duration = "0",
            drivers = emptyList(),
            route = Route.empty
        )
    }

    fun isNotEmpty() = this != empty
}

internal fun RideEstimate.asInternal() = RideEstimateEntity(
    id = 1L,
    origin = origin,
    destination = destination,
    distance = distance,
    duration = duration,
    drivers = drivers,
    route = route
)

internal fun RideEstimateEntity.asExternal() = RideEstimate(
    origin = origin ?: Coordinates.empty,
    destination = destination ?: Coordinates.empty,
    distance = distance ?: 0.0,
    duration = duration ?: "",
    drivers = drivers ?: emptyList(),
    route = route ?: Route.empty
)

internal fun RideEstimateResponse.asExternalResponse() = RideEstimate(
    origin = origin ?: Coordinates.empty,
    destination = destination ?: Coordinates.empty,
    distance = distance ?: 0.0,
    duration = duration ?: "",
    drivers = drivers ?: emptyList(),
    route = routeResponse?.routes?.firstOrNull()?.legs?.firstOrNull()?.steps?.let {
        Route(
            origin = origin ?: Coordinates.empty,
            destination = destination ?: Coordinates.empty,
            steps = it.map { step -> step.asExternal() }
        )
    } ?: Route.empty
)