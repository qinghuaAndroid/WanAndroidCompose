package com.wan.android.library.db

import androidx.room3.ColumnTypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class StringListConverter {

    @ColumnTypeConverter
    fun fromString(value: String?): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson<List<String>>(value, listType)
    }

    @ColumnTypeConverter
    fun fromList(list: List<String>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}