package com.wan.android.compose.viewmodel

import com.wan.android.compose.data.SettingRepository
import com.wan.android.library.bean.AppThemeMode
import com.wan.android.library.mvvm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingRepository: SettingRepository
) : BaseViewModel() {

    private val _appThemeMode = MutableStateFlow(AppThemeMode.FOLLOW_SYSTEM)
    val appThemeMode = _appThemeMode.asStateFlow()

    init {
        _appThemeMode.value = settingRepository.getAppThemeMode()
    }

    fun setAppThemeMode(appThemeMode: AppThemeMode) {
        _appThemeMode.value = appThemeMode
        settingRepository.setAppThemeMode(appThemeMode)
    }

}