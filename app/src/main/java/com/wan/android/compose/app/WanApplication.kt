package com.wan.android.compose.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by cy on 2019/10/21.
 *
 * 初始化操作使用jetpack-startup
 * [com.wan.android.compose.initializer.WanAppInitializer]
 */
@HiltAndroidApp
class WanApplication : Application(), ViewModelStoreOwner {

    private lateinit var mAppViewModelStore: ViewModelStore

    override fun onCreate() {
        super.onCreate()
        mAppViewModelStore = ViewModelStore()

        ProcessLifecycleOwner.get().lifecycle.addObserver(LifecycleChecker())
    }

    override val viewModelStore: ViewModelStore
        get() = mAppViewModelStore

}