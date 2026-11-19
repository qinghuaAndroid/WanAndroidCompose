package  com.example.application.app

import androidx.lifecycle.ProcessLifecycleOwner
import com.example.core.app.App
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by cy on 2019/10/21.
 *
 * 初始化操作使用jetpack-startup
 * [ com.example.myapplication.provider.WanAppInitializer]
 */
@HiltAndroidApp
class WanApplication : App() {
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(LifecycleChecker())
    }
}