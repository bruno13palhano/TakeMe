package com.bruno13palhano.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Coordinates(
    val latitude: Double,
    val longitude: Double
) {
    companion object {
        val empty = Coordinates(0.0, 0.0)
    }
}