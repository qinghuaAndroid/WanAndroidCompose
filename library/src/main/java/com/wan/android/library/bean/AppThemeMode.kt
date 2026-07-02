package com.wan.android.library.bean

enum class AppThemeMode(val value: Int) {
    FOLLOW_SYSTEM(0), // 跟随系统
    LIGHT(1),         // 始终白天
    DARK(2);           // 始终黑夜

    companion object {
        fun fromValue(value: Int): AppThemeMode {
            return entries.firstOrNull { it.value == value } ?: FOLLOW_SYSTEM
        }
    }
}