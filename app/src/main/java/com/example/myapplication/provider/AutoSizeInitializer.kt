package  com.example.myapplication.provider

import android.content.Context
import androidx.startup.Initializer
import  com.example.core.utils.AutoDensityUtils

/**
 *
 * Created by cy on 2023/7/18.
 */
class AutoSizeInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        AutoDensityUtils.init()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}