package  com.wan.android.ui.setting

import android.content.Context
import com.wan.core.mvvm.BaseRepository
import com.wan.core.utils.CacheUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SettingRepository @Inject constructor(@param:ApplicationContext private val context: Context) :
    BaseRepository() {

    fun getCacheSize(): String {
        return CacheUtils.getTotalCacheSize(context)
    }

    fun clearCache() {
        CacheUtils.clearAllCache(context)
    }
}