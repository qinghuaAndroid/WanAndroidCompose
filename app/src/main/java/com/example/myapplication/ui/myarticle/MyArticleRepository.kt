package  com.example.myapplication.ui.myarticle

import  com.example.myapplication.bean.MyArticleEntity
import  com.example.myapplication.http.ApiService
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import javax.inject.Inject

/**
 * @author cy
 * Create at 2020/4/15.
 */
class MyArticleRepository @Inject constructor(private val apiService: ApiService) :
    BaseRepository() {

    suspend fun getMyArticle(pageNum: Int): Result<MyArticleEntity> {
        return safeApiCall { requestMyArticle(pageNum) }
    }

    private suspend fun requestMyArticle(pageNum: Int): Result<MyArticleEntity> {
        return executeResponse(apiService.getMyArticle(pageNum))
    }
}