package  com.wan.android.ui.main

import  com.wan.android.bean.UserInfoEntity
import  com.wan.android.http.ApiService
import  com.wan.core.mvvm.BaseRepository
import  com.wan.core.mvvm.Result
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: ApiService): BaseRepository() {

    suspend fun getUserInfo(): Result<UserInfoEntity> {
        return safeApiCall { requestUserInfo() }
    }

    private suspend fun requestUserInfo(): Result<UserInfoEntity> {
        return executeResponse(apiService.getUserInfo())
    }
}