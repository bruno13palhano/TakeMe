package com.bruno13palhano.data.local.database

import androidx.room.TypeConverter
import com.bruno13palhano.data.model.Coordinates

internal class CoordinatesConverter {
    @TypeConverter
    fun fromCoordinates(coordinates: Coordinates): String {
        return "${coordinates.latitude},${coordinates.longitude}"
    }

    @TypeConverter
    fun toCoordinates(value: String): Coordinates {
        val parts = value.split(",")
        return Coordinates(parts[0].toDouble(), parts[1].toDouble())
    }
}