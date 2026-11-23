package com.wan.android.ui.collect

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.lightColorScheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wan.android.R
import com.wan.android.bean.ArticleEntity
import com.wan.android.ui.ArticleViewModel
import com.wan.android.ui.article.ArticleCard
import com.wan.core.ext.getThemeColor
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import com.wan.common.R as CommonR
import com.wan.core.R as CoreR

@Composable
internal fun CollectScreenTheme(
    themeColor: Color = Color(getThemeColor()),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = themeColor,
            onPrimary = Color.White,
            background = colorResource(CommonR.color.viewBackground),
            surface = colorResource(CommonR.color.viewBackground),
            onSurface = colorResource(CommonR.color.text_1),
            onSurfaceVariant = colorResource(CommonR.color.text_3),
            outlineVariant = colorResource(CommonR.color.division_line)
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CollectScreen(
    articleViewModel: ArticleViewModel,
    onOpenArticle: (ArticleEntity.DatasBean) -> Unit,
    onNavigateUp: () -> Unit
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
        articleViewModel.getCollectData(refresh)
    }

    LaunchedEffect(articleViewModel) {
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
        load(refresh = true)
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
            load(refresh = false)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CollectTopAppBar(onNavigateUp = onNavigateUp)
        }
    ) { contentPadding ->
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = { if (!refreshing) load(refresh = true) },
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(articles, key = { it.id }) { article ->
                    ArticleCard(
                        article = article,
                        onClick = { onOpenArticle(article) }
                    )
                }
                if (loadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectTopAppBar(onNavigateUp: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.nav_my_collect),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    painter = painterResource(CoreR.mipmap.ic_back_white),
                    contentDescription = stringResource(R.string.collect_back_content_description),
                    tint = Color.Unspecified
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}
