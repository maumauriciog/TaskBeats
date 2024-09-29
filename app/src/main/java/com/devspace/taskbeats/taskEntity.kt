package com.devspace.taskbeats

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class taskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String
)