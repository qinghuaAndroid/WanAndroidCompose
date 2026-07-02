package com.wan.android.library.db

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.wan.android.library.bean.User

@Database(entities = [User::class], version = 1, exportSchema = true)
@ColumnTypeConverters(value = [IntegerListConverter::class, StringListConverter::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        const val DATABASE_NAME = "WanAndroid.db"
    }
}