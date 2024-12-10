package com.bruno13palhano.data.repository

import com.bruno13palhano.data.model.DriverInfo
import kotlinx.coroutines.flow.Flow

interface DriverInfoRepository {
    suspend fun insertDriverInfo(driverInfo: DriverInfo)

    fun getAllDriverInfo(): Flow<List<DriverInfo>>
}