package com.example.application.ui.collect

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.application.ui.ArticleViewModel
import com.example.application.ui.browser.BrowserNormalActivity
import com.example.common.constant.Const
import com.example.core.utils.AutoDensityUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CollectActivity : ComponentActivity() {
    private val articleViewModel by viewModels<ArticleViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        AutoDensityUtils.setCustomDensity(this, application)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollectScreenTheme {
                CollectScreen(
                    articleViewModel = articleViewModel,
                    onOpenArticle = { article ->
                        startActivity(Intent(this, BrowserNormalActivity::class.java).apply {
                            putExtra(Const.WEB_TITLE, article.title)
                            putExtra(Const.WEB_URL, article.link)
                        })
                    },
                    onNavigateUp = onBackPressedDispatcher::onBackPressed
                )
            }
        }
    }
}
