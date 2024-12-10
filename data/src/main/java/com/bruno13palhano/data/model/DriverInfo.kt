package com.bruno13palhano.data.model

import com.bruno13palhano.data.local.model.DriverInfoEntity
import kotlinx.serialization.Serializable

@Serializable
data class DriverInfo(
    val id: Long,
    val name: String
) {
    companion object {
        val empty = DriverInfo(id = 0, name = "")
    }
}


internal fun DriverInfo.asInternal() = DriverInfoEntity(
    id = id,
    name = name
)

internal fun DriverInfoEntity.asExternal() = DriverInfo(
    id = id,
    name = name
)