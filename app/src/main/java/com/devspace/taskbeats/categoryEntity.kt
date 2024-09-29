package com.devspace.taskbeats

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class categoryEntity (
    @PrimaryKey
    @ColumnInfo("Key")
    val name: String,
    @ColumnInfo("is_Selected")
    val isSelected: Boolean
)