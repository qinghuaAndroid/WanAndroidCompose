package  com.example.myapplication.ui.share

import  com.example.myapplication.bean.ArticleEntity
import  com.example.myapplication.http.ApiService
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import javax.inject.Inject

/**
 * @author cy
 * Create at 2020/4/15.
 */
class ShareListRepository @Inject constructor(private val apiService: ApiService) :
    BaseRepository() {

    suspend fun getShareArticle(pageNum: Int): Result<ArticleEntity> {
        return safeApiCall { requestShareArticle(pageNum) }
    }

    private suspend fun requestShareArticle(pageNum: Int): Result<ArticleEntity> {
        return executeResponse(apiService.getShareArticle(pageNum))
    }
}