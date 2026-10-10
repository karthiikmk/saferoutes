package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {

    @Query("SELECT * FROM vehicles WHERE userEmail = :email ORDER BY registrationNumber ASC")
    fun getVehiclesForUser(email: String): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE userEmail = :email ORDER BY registrationNumber ASC")
    suspend fun getVehiclesForUserOnce(email: String): List<VehicleEntity>

    @Query("SELECT * FROM vehicles WHERE userEmail = :email AND isDefault = 1 LIMIT 1")
    fun getDefaultVehicle(email: String): Flow<VehicleEntity?>

    @Query("SELECT * FROM vehicles WHERE registrationNumber = :reg LIMIT 1")
    suspend fun getVehicleByReg(reg: String): VehicleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)

    @Query("UPDATE vehicles SET isDefault = 0 WHERE userEmail = :email")
    suspend fun clearDefaultVehicles(email: String)

    @Query("UPDATE vehicles SET isDefault = 1 WHERE registrationNumber = :reg")
    suspend fun setVehicleAsDefault(reg: String)

    @Transaction
    suspend fun selectActiveVehicle(reg: String, email: String) {
        clearDefaultVehicles(email)
        setVehicleAsDefault(reg)
    }
}
