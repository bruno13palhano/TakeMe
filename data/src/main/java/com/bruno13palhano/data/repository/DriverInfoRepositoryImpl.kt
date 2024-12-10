package com.bruno13palhano.data.repository

import com.bruno13palhano.data.di.DriverInfoLocalDataSource
import com.bruno13palhano.data.local.datasource.DriveInfoLocal
import com.bruno13palhano.data.model.DriverInfo
import com.bruno13palhano.data.model.asExternal
import com.bruno13palhano.data.model.asInternal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class DriverInfoRepositoryImpl @Inject constructor(
    @DriverInfoLocalDataSource private val driverInfoData: DriveInfoLocal
) : DriverInfoRepository {
    override suspend fun insertDriverInfo(driverInfo: DriverInfo) {
        driverInfoData.insert(driverInfo = driverInfo.asInternal())
    }

    override fun getAllDriverInfo(): Flow<List<DriverInfo>> {
        return driverInfoData.getAll().map {
            it.map { driver -> driver.asExternal() }
        }
    }
}