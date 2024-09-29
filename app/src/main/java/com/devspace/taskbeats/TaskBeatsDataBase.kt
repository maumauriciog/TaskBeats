package com.devspace.taskbeats

import androidx.room.Database
import androidx.room.RoomDatabase

@Database([categoryEntity::class, taskEntity::class], version = 1)
abstract class TaskBeatsDataBase : RoomDatabase(){

    abstract fun getCategoryDAO() : categoryDAO
    abstract fun getTaskDAO() : taskDAO
}