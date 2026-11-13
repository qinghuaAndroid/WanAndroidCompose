package  com.example.myapplication.ui.main

import  com.example.myapplication.bean.UserInfoEntity
import  com.example.myapplication.http.ApiService
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import javax.inject.Inject

class MainRepository @Inject constructor(private val apiService: ApiService): BaseRepository() {

    suspend fun getUserInfo(): Result<UserInfoEntity> {
        return safeApiCall { requestUserInfo() }
    }

    private suspend fun requestUserInfo(): Result<UserInfoEntity> {
        return executeResponse(apiService.getUserInfo())
    }
}