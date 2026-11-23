package com.wan.common.auth

import com.wan.common.constant.Const
import com.tencent.mmkv.MMKV

object LoginState {
    fun isLoggedIn(): Boolean = MMKV.defaultMMKV().decodeBool(Const.IS_LOGIN, false)
}
