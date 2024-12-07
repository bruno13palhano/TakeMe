package com.bruno13palhano.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bruno13palhano.data.model.Coordinates
import com.bruno13palhano.data.model.Driver
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "ride_estimates")
internal data class RideEstimateEntity(
    @PrimaryKey
    val id: Long,
    val origin: Coordinates?,
    val destination: Coordinates?,
    val distance: Double?,
    val duration: Long?,
    val drivers: List<Driver>?
)
