package com.devspace.taskbeats

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = categoryEntity::class,
            parentColumns = ["Key"],
            childColumns = ["category"]
        )
    ]
)
data class taskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String
)