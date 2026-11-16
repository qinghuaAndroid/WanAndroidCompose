package  com.example.application.ui.integral

import  com.example.application.bean.IntegralRecordEntity
import  com.example.application.http.ApiService
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import javax.inject.Inject

/**
 * @author cy
 * Create at 2020/4/10.
 */
class IntegralRepository @Inject constructor(private val apiService: ApiService) :
    BaseRepository() {

    suspend fun getIntegralRecord(pageNum: Int): Result<IntegralRecordEntity> {
        return safeApiCall { requestIntegralRecord(pageNum) }
    }

    private suspend fun requestIntegralRecord(pageNum: Int): Result<IntegralRecordEntity> {
        return executeResponse(apiService.getIntegralRecord(pageNum))
    }
}