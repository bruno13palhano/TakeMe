package com.bruno13palhano.data.local.datasource

import com.bruno13palhano.data.local.model.DriverInfoEntity
import kotlinx.coroutines.flow.Flow

internal interface DriveInfoLocal {
    suspend fun insert(driverInfo: DriverInfoEntity)

    fun getAll(): Flow<List<DriverInfoEntity>>
}