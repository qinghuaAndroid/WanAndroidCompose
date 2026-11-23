package com.wan.android.provider

import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.wan.core.log.LogUtil
import com.wan.core.storage.StorageType
import com.wan.core.storage.StorageUtil

/**
 *
 * Created by cy on 2023/7/18.
 */
class LogInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        //初始日志文件存储目录
        val directory = StorageUtil.getDirectoryByDirType(StorageType.TYPE_LOG)
        LogUtil.init(directory, Log.DEBUG)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(StorageInitializer::class.java)
    }
}