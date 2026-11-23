package com.example.application.ui.github

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.mvvm.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GitHubProjectsUiState(
    val isLoading: Boolean = false,
    val repositories: List<GitHubRepository> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class GitHubProjectsViewModel @Inject constructor(
    private val repository: GitHubProjectsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(GitHubProjectsUiState())
    val uiState: StateFlow<GitHubProjectsUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            _uiState.value = when (val result = repository.loadPopularRepositories()) {
                is Result.Success -> GitHubProjectsUiState(repositories = result.data)
                is Result.Error -> GitHubProjectsUiState(
                    errorMessage = result.exception.message ?: "GitHub request failed"
                )
            }
        }
    }
}
