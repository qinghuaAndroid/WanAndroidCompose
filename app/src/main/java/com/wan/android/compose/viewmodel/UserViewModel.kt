package com.wan.android.compose.viewmodel

import androidx.lifecycle.viewModelScope
import com.wan.android.compose.data.UserRepository
import com.wan.android.library.bean.User
import com.wan.android.library.mvvm.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    userRepository: UserRepository
) : BaseViewModel() {

    val currentUser: StateFlow<User?> = userRepository.currentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)


}