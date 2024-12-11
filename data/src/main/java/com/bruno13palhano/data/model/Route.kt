package com.bruno13palhano.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val origin: Coordinates,
    val destination: Coordinates,
    val steps: List<Step>
) {
    companion object {
        val empty = Route(
            origin = Coordinates.empty,
            destination = Coordinates.empty,
            steps = emptyList()
        )
    }
}
