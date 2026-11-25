package com.wan.android.ui.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wan.android.R
import com.wan.android.bean.ArticleEntity
import com.wan.android.ui.browser.BrowserNormalActivity
import com.wan.common.constant.Const
import com.wan.core.ext.showToast
import dagger.hilt.android.AndroidEntryPoint

/**
 * 导航页（Compose 实现）。
 *
 * @author cy
 * Create at 2020/4/8.
 */
@AndroidEntryPoint
class NavigationFragment : Fragment() {

    private val navigationViewModel by viewModels<NavigationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        // 固定 ID，让 LazyColumn 的滚动位置可以随 View 一起恢复。
        id = R.id.navigation_compose_view
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent { NavigationContent() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // uiState 是 StateFlow，旋转屏幕后数据仍在，只在首次创建时请求。
        if (savedInstanceState == null) {
            navigationViewModel.getNavigation()
        }
    }

    @Composable
    private fun NavigationContent() {
        val state by navigationViewModel.uiState.collectAsStateWithLifecycle()
        LaunchedEffect(state.errorMessage) {
            state.errorMessage?.let { message ->
                showToast(message)
                navigationViewModel.onErrorConsumed()
            }
        }
        NavigationScreenTheme {
            NavigationScreen(
                state = state,
                onOpenArticle = { article -> openArticle(article) },
                onRetry = navigationViewModel::getNavigation
            )
        }
    }

    private fun openArticle(article: ArticleEntity.DatasBean) {
        startActivity(
            Intent(requireContext(), BrowserNormalActivity::class.java).apply {
                putExtra(Const.WEB_TITLE, article.title)
                putExtra(Const.WEB_URL, article.link)
            }
        )
    }
}
