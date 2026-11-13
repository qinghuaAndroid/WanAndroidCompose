package  com.example.myapplication.ui.home

import  com.example.myapplication.bean.ArticleEntity
import  com.example.myapplication.bean.BannerEntity
import  com.example.myapplication.http.ApiService
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import javax.inject.Inject

/**
 * @author cy
 * Create at 2020/4/16.
 */
class HomeRepository @Inject constructor(private val apiService: ApiService) : BaseRepository() {

    suspend fun loadTopArticles(): Result<MutableList<ArticleEntity.DatasBean>> {
        return safeApiCall { requestTopArticles() }
    }

    suspend fun loadArticles(pageNum: Int): Result<ArticleEntity> {
        return safeApiCall { requestArticles(pageNum) }
    }

    suspend fun loadBanner(): Result<MutableList<BannerEntity>> {
        return safeApiCall { requestBanner() }
    }

    private suspend fun requestTopArticles(): Result<MutableList<ArticleEntity.DatasBean>> {
        return executeResponse(apiService.getTopList())
    }

    private suspend fun requestArticles(pageNum: Int): Result<ArticleEntity> {
        return executeResponse(apiService.getHomeList(pageNum))
    }

    private suspend fun requestBanner(): Result<MutableList<BannerEntity>> {
        return executeResponse(apiService.getBanner())
    }
}