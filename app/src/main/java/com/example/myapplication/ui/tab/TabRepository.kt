package  com.example.myapplication.ui.tab

import  com.example.myapplication.bean.TabEntity
import  com.example.myapplication.constant.Const
import  com.example.myapplication.http.ApiService
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import javax.inject.Inject


class TabRepository @Inject constructor(private val apiService: ApiService) : BaseRepository() {

    suspend fun getTabList(type: Int): Result<MutableList<TabEntity>> {
        return safeApiCall { requestTabList(type) }
    }

    private suspend fun requestTabList(type: Int): Result<MutableList<TabEntity>> {
        val result = if (type == Const.PROJECT_TYPE) {
            apiService.getProjectTabList()
        } else {
            apiService.getAccountTabList()
        }
        return executeResponse(result)
    }
}