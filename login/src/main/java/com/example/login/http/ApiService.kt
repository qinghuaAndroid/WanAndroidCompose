package com.example.login.http

import com.example.core.network.HttpResult
import com.example.login.bean.User
import retrofit2.http.*

/**
 * @author cy
 * Create at 2020/4/2.
 */
interface ApiService {
    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): HttpResult<User>

    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("/user/register")
    suspend fun register(
        @Query("username") username: String,
        @Query("password") password: String,
        @Query("repassword") repassword: String
    ): HttpResult<Any>

}