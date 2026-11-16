package  com.example.application.ui.tab.list

import  com.example.application.bean.ArticleEntity
import  com.example.application.http.ApiService
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import javax.inject.Inject

/**
 * @author cy
 * Create at 2020/4/16.
 */
class TabListRepository @Inject constructor(private val apiService: ApiService) : BaseRepository() {

    suspend fun getProjectList(pageNum: Int, id: Int): Result<ArticleEntity> {
        return safeApiCall { requestProjectList(pageNum, id) }
    }

    private suspend fun requestProjectList(pageNum: Int, id: Int): Result<ArticleEntity> {
        return executeResponse(apiService.getProjectList(pageNum, id))
    }

    suspend fun getAccountList(id: Int, pageNum: Int): Result<ArticleEntity> {
        return safeApiCall { requestAccountList(id, pageNum) }
    }

    private suspend fun requestAccountList(id: Int, pageNum: Int): Result<ArticleEntity> {
        return executeResponse(apiService.getAccountList(id, pageNum))
    }
}