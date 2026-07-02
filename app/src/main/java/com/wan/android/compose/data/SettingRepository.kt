package com.wan.android.compose.data

import com.tencent.mmkv.MMKV
import com.wan.android.compose.constant.StorageKey
import com.wan.android.library.bean.AppThemeMode
import com.wan.android.library.mvvm.BaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingRepository @Inject constructor(
    private val mmkv: MMKV
) : BaseRepository() {

    fun setAppThemeMode(appThemeMode: AppThemeMode) {
        mmkv.encode(StorageKey.APP_THEME_MODE, appThemeMode.value)
    }

    fun getAppThemeMode(): AppThemeMode {
        val value = mmkv.decodeInt(
            StorageKey.APP_THEME_MODE,
            AppThemeMode.FOLLOW_SYSTEM.value
        )
        return AppThemeMode.fromValue(value)
    }
}
