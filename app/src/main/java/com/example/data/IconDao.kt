package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IconDao {
    // Requests
    @Query("SELECT * FROM icon_requests ORDER BY timestamp DESC")
    fun getAllRequests(): Flow<List<RequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: RequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequests(requests: List<RequestEntity>)

    @Query("DELETE FROM icon_requests WHERE packageName = :packageName")
    suspend fun deleteRequestByPackage(packageName: String)

    @Query("DELETE FROM icon_requests")
    suspend fun deleteAllRequests()

    // Favorites
    @Query("SELECT * FROM icon_favorites ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM icon_favorites WHERE iconId = :iconId")
    suspend fun deleteFavoriteById(iconId: String)
}
