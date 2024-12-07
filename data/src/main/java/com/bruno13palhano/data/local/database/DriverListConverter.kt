package com.bruno13palhano.data.local.database

import androidx.room.TypeConverter
import com.bruno13palhano.data.model.Driver
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class DriverListConverter {
    @TypeConverter
    fun fromDriverList(driverList: List<Driver>): String {
        return Json.encodeToString(driverList)
    }

    @TypeConverter
    fun toDriverList(value: String): List<Driver> {
        return Json.decodeFromString(value)
    }
}