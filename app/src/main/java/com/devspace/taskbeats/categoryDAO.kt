package com.devspace.taskbeats

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface categoryDAO {
    @Query("select * From categoryEntity")
    fun getAll():List<categoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll (categoryEntity: List<categoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert (categoryEntity: categoryEntity)

    @Delete
    fun delete (categoryEntity: categoryEntity)
}