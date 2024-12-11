package com.bruno13palhano.data.local.database

import androidx.room.TypeConverter
import com.bruno13palhano.data.model.Route
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class RouteConverter {
    @TypeConverter
    fun fromRoute(route: Route): String {
        return Json.encodeToString(route)
    }

    @TypeConverter
    fun toRoute(value: String): Route {
        return Json.decodeFromString(value)
    }
}