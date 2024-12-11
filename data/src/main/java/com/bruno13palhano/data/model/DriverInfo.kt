package com.bruno13palhano.data.model

import com.bruno13palhano.data.local.model.DriverInfoEntity
import kotlinx.serialization.Serializable

@Serializable
data class DriverInfo(
    val id: Long,
    val name: String,
    val minKm: Float?,
) {
    companion object {
        val empty = DriverInfo(id = 0, name = "", minKm = 0f)
    }
}


internal fun DriverInfo.asInternal() = DriverInfoEntity(
    id = id,
    name = name,
    minKm = minKm ?: 0f
)

internal fun DriverInfoEntity.asExternal() = DriverInfo(
    id = id,
    name = name,
    minKm = minKm
)