package com.wan.android.library.network.exception

/**
 * Created by xuhao on 2017/12/5.
 * desc:
 */
object ErrorStatus {
    /**
     * 响应成功
     */
    const val SUCCESS = 0

    /**
     * 未知错误
     */
    const val UNKNOWN_ERROR = 1002

    /**
     * 服务器内部错误
     */
    const val SERVER_ERROR = 1003

    /**
     * 网络连接超时
     */
    const val NETWORK_ERROR = 1004

    /**
     * 未登录
     */
    const val LOGOUT_ERROR = -1001

    /**
     * 其它服务器定义的错误
     */
}