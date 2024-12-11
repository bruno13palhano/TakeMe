package com.bruno13palhano.data.model

import com.bruno13palhano.data.local.model.RideEstimateEntity
import com.bruno13palhano.data.remote.model.response.RideEstimateResponse

data class RideEstimate(
    val distance: Double,
    val duration: String,
    val drivers: List<Driver>,
    val route: Route
) {
    companion object {
        val empty = RideEstimate(
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
    distance = distance,
    duration = duration,
    drivers = drivers,
    route = route
)

internal fun RideEstimateEntity.asExternal() = RideEstimate(
    distance = distance ?: 0.0,
    duration = duration ?: "",
    drivers = drivers ?: emptyList(),
    route = route ?: Route.empty
)

internal fun RideEstimateResponse.asExternalResponse() = RideEstimate(
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