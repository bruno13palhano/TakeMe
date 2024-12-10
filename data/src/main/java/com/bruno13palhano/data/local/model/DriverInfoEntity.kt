package com.bruno13palhano.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "driver_info")
data class DriverInfoEntity(
    @PrimaryKey
    val id: Long,
    val name: String
)