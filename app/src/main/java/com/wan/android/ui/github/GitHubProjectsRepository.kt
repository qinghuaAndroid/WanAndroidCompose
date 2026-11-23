package com.wan.android.ui.github

import com.wan.core.mvvm.Result
import com.wan.core.network.exception.ApiException
import javax.inject.Inject

class GitHubProjectsRepository @Inject constructor(
    private val api: GitHubApi
) {
    suspend fun loadPopularRepositories(): Result<List<GitHubRepository>> {
        return try {
            val response = api.searchRepositories(query = "stars:>5000")
            val body = response.body()
            if (response.isSuccessful && body != null) {
                Result.Success(body.items)
            } else {
                Result.Error(ApiException(response.code(), "GitHub request failed"))
            }
        } catch (exception: Exception) {
            Result.Error(ApiException(exception.message, exception))
        }
    }
}
