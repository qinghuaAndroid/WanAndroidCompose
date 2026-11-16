package  com.example.application.ui.question

import  com.example.application.bean.ArticleEntity
import  com.example.application.http.ApiService
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import javax.inject.Inject

/**
 * @author cy
 * Create at 2020/4/15.
 */
class QuestionRepository @Inject constructor(private val apiService: ApiService) :
    BaseRepository() {

    suspend fun getQuestionList(pageNum: Int): Result<ArticleEntity> {
        return safeApiCall { requestQuestionList(pageNum) }
    }

    private suspend fun requestQuestionList(pageNum: Int): Result<ArticleEntity> {
        return executeResponse(apiService.getQuestionList(pageNum))
    }
}