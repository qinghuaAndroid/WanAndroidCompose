package com.wan.android.ui.navigation

import androidx.lifecycle.viewModelScope
import com.wan.android.bean.NavigationEntity
import com.wan.core.mvvm.BaseViewModel
import com.wan.core.mvvm.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author cy
 * Create at 2020/4/8.
 */
@HiltViewModel
class NavigationViewModel @Inject constructor(private val mRepository: NavigationRepository) :
    BaseViewModel() {

    private val _uiState = MutableStateFlow(NavigationUiState())
    val uiState: StateFlow<NavigationUiState> = _uiState.asStateFlow()

    fun getNavigation() {
        // 避免旋转屏幕或重复触发时发起多次请求。
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = mRepository.getNavigation()
            _uiState.update { state ->
                when (result) {
                    is Result.Success -> state.copy(
                        isLoading = false,
                        items = result.data.toList()
                    )

                    is Result.Error -> state.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                }
            }
        }
    }

    /** 错误提示只消费一次，避免重组时重复弹出 Toast。 */
    fun onErrorConsumed() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class NavigationUiState(
    val isLoading: Boolean = false,
    val items: List<NavigationEntity> = emptyList(),
    val errorMessage: String? = null
)
