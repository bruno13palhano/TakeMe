package com.bruno13palhano.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Driver(
    val id: Long,
    val name: String?,
    val description: String?,
    val vehicle: String?,
    val review: Review?,
    val value: Float?
)