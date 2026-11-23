package  com.example.application.ui.collect

import androidx.lifecycle.viewModelScope
import com.example.application.bean.ArticleEntity
import com.example.common.base.BaseUiState
import com.example.core.mvvm.BaseViewModel
import com.example.core.mvvm.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CollectViewModel @Inject constructor(private val mRepository: CollectRepository) :
    BaseViewModel() {

    private val _uiState = MutableSharedFlow<BaseUiState<ArticleEntity.DatasBean>>()
    val uiState: SharedFlow<BaseUiState<ArticleEntity.DatasBean>>
        get() = _uiState

    fun collect(datasBean: ArticleEntity.DatasBean) {
        viewModelScope.launch {
            val result = mRepository.collect(datasBean.id)
            if (result is Result.Success) emitUiState(showSuccess = datasBean.apply {
                collect = true
            })
            else if (result is Result.Error) emitUiState(showError = result.exception.message)
        }
    }

    fun unCollect(datasBean: ArticleEntity.DatasBean) {
        viewModelScope.launch {
            val result = mRepository.unCollect(datasBean.id)
            if (result is Result.Success) emitUiState(showSuccess = datasBean.apply {
                collect = false
            })
            else if (result is Result.Error) emitUiState(showError = result.exception.message)
        }
    }

    fun unMyCollect(datasBean: ArticleEntity.DatasBean) {
        viewModelScope.launch {
            val result = mRepository.unMyCollect(datasBean.id, datasBean.originId)
            if (result is Result.Success) emitUiState(showSuccess = datasBean.apply {
                collect = false
            })
            else if (result is Result.Error) emitUiState(showError = result.exception.message)
        }
    }

    private suspend fun emitUiState(
        showLoading: Boolean = false,
        showError: String? = null,
        showSuccess: ArticleEntity.DatasBean? = null
    ) {
        val baseUiState = BaseUiState(showLoading, showError, showSuccess)
        _uiState.emit(baseUiState)
    }
}