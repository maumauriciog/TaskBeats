package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface taskDAO {
    @Query("select * From taskEntity")
    fun getAllTask(): List<taskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTask(taskEntity: List<taskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(taskEntity: taskEntity)

    @Delete
    fun delete(taskEntity: taskEntity)

    @Query("Select * From taskEntity where category is :categoryName")
    fun getAllByCategoryName(categoryName: String): List<taskEntity>

    @Delete
    fun deleteAll(taskEntity: List<taskEntity>)
}