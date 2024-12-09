package com.bruno13palhano.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DriverInfo(
    val id: Long,
    val name: String
)
