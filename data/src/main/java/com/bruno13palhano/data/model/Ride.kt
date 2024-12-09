package com.bruno13palhano.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Ride(
    val id: Long,
    val date: String,
    val origin: String,
    val destination: String,
    val distance: Double,
    val duration: String,
    val driver: DriverInfo,
    val value: Float
)