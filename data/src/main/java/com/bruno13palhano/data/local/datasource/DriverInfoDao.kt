package com.bruno13palhano.data.local.datasource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bruno13palhano.data.local.model.DriverInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface DriverInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(driverInfo: DriverInfoEntity)

    @Query("SELECT * FROM driver_info WHERE id = :id")
    suspend fun getDriverInfo(id: Long): DriverInfoEntity?

    @Query("SELECT * FROM driver_info")
    fun getAll(): Flow<List<DriverInfoEntity>>
}