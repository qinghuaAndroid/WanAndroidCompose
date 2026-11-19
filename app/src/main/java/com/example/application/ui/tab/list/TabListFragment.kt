package com.example.application.ui.tab.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.example.application.R
import com.example.application.bean.ArticleEntity
import com.example.application.constant.Const
import com.example.application.ui.ArticleViewModel
import com.example.application.ui.browser.BrowserNormalActivity
import com.example.application.ui.collect.CollectViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import com.example.common.constant.Const as CommonConst
import com.example.common.R as CommonR
import com.example.core.R as CoreR

@AndroidEntryPoint
class TabListFragment : Fragment() {

    private val articleViewModel by viewModels<ArticleViewModel>()
    private val collectViewModel by viewModels<CollectViewModel>()

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
                    articleViewModel = articleViewModel,
                    collectViewModel = collectViewModel
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
    articleViewModel: ArticleViewModel,
    collectViewModel: CollectViewModel
) {
    val context = LocalContext.current
    val articles = remember { mutableStateListOf<ArticleEntity.DatasBean>() }
    val listState = rememberLazyListState()
    var refreshing by remember { mutableStateOf(false) }
    var loadingMore by remember { mutableStateOf(false) }
    var loadMoreEnabled by remember { mutableStateOf(true) }
    var reachedEnd by remember { mutableStateOf(false) }
    var collectingArticleId by remember { mutableStateOf<Int?>(null) }

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

    LaunchedEffect(articleViewModel, collectViewModel, type, projectId) {
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
        launch(start = CoroutineStart.UNDISPATCHED) {
            collectViewModel.uiState.collect { state ->
                state.showSuccess?.let { collected ->
                    val index = articles.indexOfFirst { it.id == collectingArticleId }
                    if (index >= 0) articles[index] = articles[index].copy(collect = collected)
                    collectingArticleId = null
                }
                state.showError?.let { message ->
                    collectingArticleId = null
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                ArticleRow(
                    article = article,
                    collecting = collectingArticleId == article.id,
                    onClick = {
                        context.startActivity(
                            Intent(context, BrowserNormalActivity::class.java).apply {
                                putExtra(CommonConst.WEB_TITLE, article.title)
                                putExtra(CommonConst.WEB_URL, article.link)
                            }
                        )
                    },
                    onCollectClick = {
                        collectingArticleId = article.id
                        if (article.collect) {
                            collectViewModel.unCollect(article.id)
                        } else {
                            collectViewModel.collect(article.id)
                        }
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

@Composable
private fun ArticleRow(
    article: ArticleEntity.DatasBean,
    collecting: Boolean,
    onClick: () -> Unit,
    onCollectClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (article.type == 1) {
                Text(
                    text = "置顶 ",
                    color = colorResource(CommonR.color.red),
                    fontSize = 11.sp
                )
            }
            Text(
                text = article.author.takeUnless { it.isNullOrBlank() }.orEmpty()
                    .ifBlank { article.shareUser.orEmpty() },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = article.niceDate.orEmpty(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (!article.envelopePic.isNullOrBlank()) {
                AsyncImage(
                    model = article.envelopePic,
                    contentDescription = null,
                    modifier = Modifier.size(width = 100.dp, height = 160.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(CoreR.mipmap.ic_default_img),
                    error = painterResource(CoreR.mipmap.ic_default_img)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = article.title.fromHtml(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                if (!article.projectLink.isNullOrBlank()) {
                    Text(
                        text = article.desc.fromHtml(),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = article.superChapterName.orEmpty(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(
                    if (article.collect) R.mipmap.article_collect
                    else R.mipmap.article_un_collect
                ),
                contentDescription = if (article.collect) "取消收藏" else "收藏",
                modifier = Modifier
                    .size(30.dp)
                    .padding(5.dp)
                    .alpha(if (collecting) 0.5f else 1f)
                    .clickable(enabled = !collecting, onClick = onCollectClick)
            )
        }
        HorizontalDivider(
            color = colorResource(CommonR.color.division_line),
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}

private fun String?.fromHtml(): String =
    HtmlCompat.fromHtml(orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
