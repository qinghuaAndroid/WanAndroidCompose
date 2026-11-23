package com.wan.android.ui.tab.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.wan.android.bean.ArticleEntity
import com.wan.android.constant.Const
import com.wan.android.ui.ArticleViewModel
import com.wan.android.ui.article.ArticleCard
import com.wan.android.ui.browser.BrowserNormalActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import com.wan.common.constant.Const as CommonConst

@AndroidEntryPoint
class TabListFragment : Fragment() {

    private val articleViewModel by viewModels<ArticleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val type = arguments?.getInt(Const.TYPE)
            val projectId = arguments?.getInt(Const.ID) ?: 0
            MaterialTheme {
                TabListScreen(
                    type = type,
                    projectId = projectId,
                    articleViewModel = articleViewModel
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TabListScreen(
    type: Int?,
    projectId: Int,
    articleViewModel: ArticleViewModel
) {
    val context = LocalContext.current
    val articles = remember { mutableStateListOf<ArticleEntity.DatasBean>() }
    val listState = rememberLazyListState()
    var refreshing by remember { mutableStateOf(false) }
    var loadingMore by remember { mutableStateOf(false) }
    var loadMoreEnabled by remember { mutableStateOf(true) }
    var reachedEnd by remember { mutableStateOf(false) }

    fun load(refresh: Boolean) {
        if (refresh) {
            refreshing = true
            reachedEnd = false
        } else {
            loadingMore = true
        }
        if (type == Const.PROJECT_TYPE) {
            articleViewModel.getProjectList(refresh, projectId)
        } else {
            articleViewModel.getAccountList(refresh, projectId)
        }
    }

    LaunchedEffect(articleViewModel, type, projectId) {
        launch(start = CoroutineStart.UNDISPATCHED) {
            articleViewModel.uiState.collect { state ->
                if (state.showLoading) refreshing = true
                state.showSuccess?.let { entity ->
                    val data = entity.datas.orEmpty()
                    if (state.isRefresh) articles.clear()
                    articles.addAll(data)
                    refreshing = false
                    loadingMore = false
                    if (entity.curPage >= entity.pageCount) reachedEnd = true
                }
                state.showError?.let { message ->
                    refreshing = false
                    loadingMore = false
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
                loadMoreEnabled = state.isEnableLoadMore
                if (state.showEnd) {
                    reachedEnd = true
                    loadingMore = false
                }
            }
        }
        load(true)
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                ?: return@derivedStateOf false
            lastVisible >= articles.lastIndex - 3
        }
    }
    LaunchedEffect(shouldLoadMore, loadingMore, loadMoreEnabled, reachedEnd, refreshing) {
        if (
            shouldLoadMore &&
            !loadingMore &&
            loadMoreEnabled &&
            !reachedEnd &&
            !refreshing &&
            articles.isNotEmpty()
        ) {
            load(false)
        }
    }

    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = { if (!refreshing) load(true) },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(articles, key = { it.id }) { article ->
                ArticleCard(
                    article = article,
                    onClick = {
                        context.startActivity(
                            Intent(context, BrowserNormalActivity::class.java).apply {
                                putExtra(CommonConst.WEB_TITLE, article.title)
                                putExtra(CommonConst.WEB_URL, article.link)
                            }
                        )
                    }
                )
            }
            if (loadingMore) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}
