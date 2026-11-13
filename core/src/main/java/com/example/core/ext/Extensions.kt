package com.example.core.ext

import com.example.core.R
import com.example.core.utils.SettingUtil
import com.example.core.utils.ToastUtils
import splitties.init.appCtx
import splitties.resources.color

/**
 * toast
 */

fun showToast(content: String) {
    ToastUtils.showShortToast(content)
}

fun showLongToast(content: String) {
    ToastUtils.showLongToast(content)
}

fun getThemeColor() = if (SettingUtil.getIsNightMode()) {
    appCtx.color(R.color.Grey800)
} else {
    SettingUtil.getColor()
}