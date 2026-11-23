package com.wan.android.ui.github

import com.google.gson.annotations.SerializedName

data class GitHubSearchResponse(
    @SerializedName("total_count") val totalCount: Int,
    val items: List<GitHubRepository>
)

data class GitHubRepository(
    val name: String,
    @SerializedName("full_name") val fullName: String,
    val description: String?,
    @SerializedName("html_url") val htmlUrl: String,
    @SerializedName("stargazers_count") val stargazersCount: Int,
    @SerializedName("forks_count") val forksCount: Int,
    val language: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val owner: GitHubOwner
)

data class GitHubOwner(
    val login: String,
    @SerializedName("avatar_url") val avatarUrl: String
)
