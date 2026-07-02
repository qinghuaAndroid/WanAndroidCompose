package com.wan.android.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wan.android.compose.ui.theme.WanAndroidComposeTheme
import com.wan.android.compose.viewmodel.SettingViewModel
import com.wan.android.compose.viewmodel.UserViewModel
import com.wan.android.library.bean.AppThemeMode
import com.wan.android.library.bean.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: SettingViewModel = viewModel()
            val currentThemeMode = viewModel.appThemeMode.collectAsStateWithLifecycle()

            val isDarkTheme = when (currentThemeMode.value) {
                AppThemeMode.FOLLOW_SYSTEM -> isSystemInDarkTheme()
                AppThemeMode.LIGHT -> false
                AppThemeMode.DARK -> true
            }

            WanAndroidComposeTheme(
                darkTheme = isDarkTheme
            ) {
                val viewModel: UserViewModel = viewModel()
                val currentUser = viewModel.currentUser.collectAsState().value
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (currentUser == null) {
                        LoginPage(modifier = Modifier.padding(innerPadding))
                    } else {
                        HomePage(
                            user = currentUser,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomePage(
    user: User,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Home页面：${user.nickname ?: user.username ?: "已登录"}",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun LoginPage(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Login页面：请先登录",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}