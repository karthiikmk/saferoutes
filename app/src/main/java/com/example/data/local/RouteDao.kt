package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Query("SELECT * FROM saved_routes ORDER BY createdAt DESC")
    fun getAllSavedRoutes(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM saved_routes WHERE userEmail = :email OR userEmail = '' ORDER BY createdAt DESC")
    fun getSavedRoutesForUser(email: String): Flow<List<RouteEntity>>

    @Query("SELECT * FROM saved_routes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteRoutes(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM saved_routes WHERE id = :id LIMIT 1")
    suspend fun getRouteById(id: String): RouteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoute(route: RouteEntity)

    @Update
    suspend fun updateRoute(route: RouteEntity)

    @Query("DELETE FROM saved_routes WHERE id = :id")
    suspend fun deleteRouteById(id: String)

    @Query("UPDATE saved_routes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)
}
