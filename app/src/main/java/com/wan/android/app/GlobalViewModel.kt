package  com.wan.android.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class GlobalViewModel(application: Application):AndroidViewModel(application) {
    private val _themeColor = MutableSharedFlow<Int>()

    val themeColor: SharedFlow<Int> = _themeColor

    fun changeThemeColor(color: Int) {
        viewModelScope.launch {
            _themeColor.emit(color)
        }
    }
}