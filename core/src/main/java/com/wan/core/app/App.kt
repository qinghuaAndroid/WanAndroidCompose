package com.wan.core.app

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.bumptech.glide.Glide

open class App : Application(), ViewModelStoreOwner {

    private lateinit var mAppViewModelStore: ViewModelStore

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //application初始化必须在这里， 不能放在onCreate中，
        //否则部分初始化依赖Application的第三方库初始化时会奔溃
    }

    override fun onCreate() {
        super.onCreate()
        mAppViewModelStore = ViewModelStore()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        // clear Glide cache
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            Glide.get(this).clearMemory()
        }
        // trim memory
        Glide.get(this).trimMemory(level)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        // low memory clear Glide cache
        Glide.get(this).clearMemory()
    }

    override val viewModelStore: ViewModelStore
        get() = mAppViewModelStore
}