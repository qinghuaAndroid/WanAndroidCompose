package com.wan.android.library.db

import androidx.room3.ColumnTypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class IntegerListConverter {

    @ColumnTypeConverter
    fun fromString(value: String?): List<Int>? {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson<List<Int>>(value, listType)
    }

    @ColumnTypeConverter
    fun fromList(list: List<Int>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}