package com.wan.android.compose.initializer

import android.content.Context
import androidx.startup.Initializer
import kotlin.jvm.java


/**
 * Created by cy on 28/6/2021.
 */
class WanAppInitializer : Initializer<Unit> {
    override fun create(context: Context) {

    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(MMKVInitializer::class.java)
    }
}