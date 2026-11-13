package  com.example.myapplication.ui.system

import  com.example.myapplication.bean.SystemListEntity
import  com.example.myapplication.http.ApiService
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import javax.inject.Inject

/**
 * @author cy
 * Create at 2020/4/8.
 */
class SystemListRepository @Inject constructor(private val apiService: ApiService) :
    BaseRepository() {

    suspend fun getSystemList(): Result<MutableList<SystemListEntity>> {
        return safeApiCall { requestSystemList() }
    }

    private suspend fun requestSystemList(): Result<MutableList<SystemListEntity>> {
        return executeResponse(apiService.getSystemList())
    }
}