package  com.example.application.ui.search.list

import  com.example.application.bean.ArticleEntity
import  com.example.application.http.ApiService
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import javax.inject.Inject

/**
 * @author cy
 * Create at 2020/4/8.
 */
class SearchListRepository @Inject constructor(private val apiService: ApiService) :
    BaseRepository() {

    suspend fun queryBySearchKey(pageNum: Int, key: String): Result<ArticleEntity> {
        return safeApiCall { requestSearchArticle(pageNum, key) }
    }

    private suspend fun requestSearchArticle(pageNum: Int, key: String): Result<ArticleEntity> {
        return executeResponse(apiService.queryBySearchKey(pageNum, key))
    }
}