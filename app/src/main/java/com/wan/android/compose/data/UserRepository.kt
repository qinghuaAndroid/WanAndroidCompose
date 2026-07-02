package com.wan.android.compose.data

import android.content.Context
import com.wan.android.compose.constant.StorageKey
import com.wan.android.library.bean.User
import com.wan.android.library.db.UserDao
import com.wan.android.library.di.getMMKV
import com.wan.android.library.mvvm.BaseRepository
import com.wan.android.library.network.service.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
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
        return executeResponse(response).onSuccess {
            getMMKV(context).run {
                encode(StorageKey.IS_LOGIN, true)
                userDao.saveUser(response.data)
            }
        }
    }

    fun currentUser(): Flow<User?> = userDao.getUser()
}
