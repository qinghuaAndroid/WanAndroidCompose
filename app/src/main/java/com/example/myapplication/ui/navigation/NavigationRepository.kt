package  com.example.myapplication.ui.navigation

import  com.example.myapplication.bean.NavigationEntity
import  com.example.myapplication.http.ApiService
import  com.example.core.mvvm.BaseRepository
import  com.example.core.mvvm.Result
import javax.inject.Inject

/**
 * @author cy
 * Create at 2020/4/8.
 */
class NavigationRepository @Inject constructor(private val apiService: ApiService) :
    BaseRepository() {

    suspend fun getNavigation(): Result<MutableList<NavigationEntity>> {
        return safeApiCall { requestNavigation() }
    }

    private suspend fun requestNavigation(): Result<MutableList<NavigationEntity>> {
        return executeResponse(apiService.getNavigation())
    }
}