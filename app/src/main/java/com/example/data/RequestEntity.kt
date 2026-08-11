package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icon_requests")
data class RequestEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Requested"
)
