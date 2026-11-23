package com.wan.core.ext

import com.wan.core.R
import com.wan.core.utils.SettingUtil
import com.wan.core.utils.ToastUtils
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