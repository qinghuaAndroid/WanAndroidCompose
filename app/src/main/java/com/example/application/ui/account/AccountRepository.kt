package  com.example.application.ui.account

import android.content.Context
import  com.example.application.http.ApiService
import  com.example.core.di.getMMKV
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import  com.example.common.constant.Const
import  com.example.login.bean.User
import  com.example.login.db.UserDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) : BaseRepository() {
    @Inject
    @ApplicationContext
    lateinit var context: Context

    fun getUser(): Flow<User?> {
        return userDao.getUser()
    }

    suspend fun logout(): Result<Any> {
        return safeApiCall { requestLogout() }
    }

    private suspend fun requestLogout(): Result<Any> {
        return executeResponse(apiService.logout(), {
            getMMKV(context).apply {
                encode(Const.IS_LOGIN, false)
                userDao.deleteUser()
            }
        })
    }
}