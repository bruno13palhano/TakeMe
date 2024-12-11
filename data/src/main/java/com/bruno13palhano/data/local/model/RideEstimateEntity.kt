package com.bruno13palhano.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bruno13palhano.data.model.Driver
import com.bruno13palhano.data.model.Route
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "ride_estimates")
internal data class RideEstimateEntity(
    @PrimaryKey
    val id: Long,
    val distance: Double?,
    val duration: String?,
    val drivers: List<Driver>?,
    val route: Route?
)
