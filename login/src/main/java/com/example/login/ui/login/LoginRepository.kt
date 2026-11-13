package com.example.login.ui.login

import android.content.Context
import com.example.common.constant.Const
import com.example.core.di.getMMKV
import com.example.core.mvvm.BaseRepository
import com.example.core.mvvm.Result
import com.example.login.bean.User
import com.example.login.db.UserDao
import com.example.login.http.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * @author cy
 * Create at 2020/4/7.
 */
class LoginRepository @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) : BaseRepository() {
    @Inject
    @ApplicationContext
    lateinit var context: Context

    suspend fun login(username: String, password: String): Result<User> {
        return safeApiCall(call = { requestLogin(username, password) })
    }

    private suspend fun requestLogin(username: String, password: String): Result<User> {
        val response = apiService.login(username, password)
        return executeResponse(response, {
            getMMKV(context).run {
                encode(Const.IS_LOGIN, true)
                userDao.saveUser(response.data)
            }
        })
    }
}