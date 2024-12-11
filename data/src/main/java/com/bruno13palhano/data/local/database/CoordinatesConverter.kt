package com.bruno13palhano.data.local.database

import androidx.room.TypeConverter
import com.bruno13palhano.data.model.Coordinates
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class CoordinatesConverter {
    @TypeConverter
    fun fromCoordinates(coordinates: Coordinates): String {
        return Json.encodeToString(coordinates)
    }

    @TypeConverter
    fun toCoordinates(value: String): Coordinates {
        return Json.decodeFromString(value)
    }
}