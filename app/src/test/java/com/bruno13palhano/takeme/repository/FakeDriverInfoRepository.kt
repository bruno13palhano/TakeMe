package com.bruno13palhano.takeme.repository

import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.repository.DriverInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeDriverInfoRepository : DriverInfoRepository {
    private val drivers = mutableListOf<DriverInfo>()

    override suspend fun insertDriverInfo(driverInfo: DriverInfo) {
        drivers.add(driverInfo)
    }

    override fun getAllDriverInfo(): Flow<List<DriverInfo>> {
        return flowOf(drivers)
    }
}