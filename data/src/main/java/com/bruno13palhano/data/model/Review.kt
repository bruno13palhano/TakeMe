package com.bruno13palhano.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val rating: Float,
    val comment: String
)