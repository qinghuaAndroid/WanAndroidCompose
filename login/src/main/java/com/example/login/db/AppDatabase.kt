package com.example.login.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.login.bean.User

@Database(entities = [User::class], version = 1, exportSchema = true)
@TypeConverters(value = [IntegerListConverter::class, StringListConverter::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "WanAndroid.db"
    }
}