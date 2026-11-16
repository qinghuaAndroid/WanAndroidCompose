package  com.example.application.ui.setting

import android.content.Context
import  com.example.core.mvvm.BaseRepository
import  com.example.core.utils.CacheUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SettingRepository @Inject constructor(@ApplicationContext private val context: Context) :
    BaseRepository() {

    fun getCacheSize(): String {
        return CacheUtils.getTotalCacheSize(context)
    }

    fun clearCache() {
        CacheUtils.clearAllCache(context)
    }
}