package com.example.common.auth

import com.example.common.constant.Const
import com.tencent.mmkv.MMKV

object LoginState {
    fun isLoggedIn(): Boolean = MMKV.defaultMMKV().decodeBool(Const.IS_LOGIN, false)
}
