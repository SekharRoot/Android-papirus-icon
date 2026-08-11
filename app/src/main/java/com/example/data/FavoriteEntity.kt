package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icon_favorites")
data class FavoriteEntity(
    @PrimaryKey val iconId: String,
    val timestamp: Long = System.currentTimeMillis()
)
