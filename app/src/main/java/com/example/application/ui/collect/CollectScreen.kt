package com.example.application.ui.collect

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import com.example.application.R
import com.example.application.bean.ArticleEntity
import com.example.application.ui.ArticleViewModel
import com.example.core.ext.getThemeColor
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import com.example.common.R as CommonR
import com.example.core.R as CoreR

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
    collectViewModel: CollectViewModel,
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
    var removingArticleId by remember { mutableStateOf<Int?>(null) }

    fun load(refresh: Boolean) {
        if (refresh) {
            refreshing = true
            reachedEnd = false
        } else {
            loadingMore = true
        }
        articleViewModel.getCollectData(refresh)
    }

    LaunchedEffect(articleViewModel, collectViewModel) {
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
                state.showSuccess?.let { datasBean ->
                    if (!datasBean.collect) {
                        articles.removeAll { it.id == removingArticleId }
                    }
                    removingArticleId = null
                }
                state.showError?.let { message ->
                    removingArticleId = null
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                    CollectArticleRow(
                        article = article,
                        removing = removingArticleId == article.id,
                        onOpenArticle = { onOpenArticle(article) },
                        onRemove = {
                            removingArticleId = article.id
                            collectViewModel.unMyCollect(article)
                        }
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

@Composable
internal fun CollectArticleRow(
    article: ArticleEntity.DatasBean,
    removing: Boolean,
    onOpenArticle: () -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenArticle)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
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
                Spacer(Modifier.width(5.dp))
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = article.superChapterName.orEmpty(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onRemove,
                    enabled = !removing
                ) {
                    Image(
                        painter = painterResource(R.mipmap.article_collect),
                        contentDescription = stringResource(R.string.collect_remove_content_description),
                        modifier = Modifier
                            .size(20.dp)
                            .alpha(if (removing) 0.5f else 1f)
                    )
                }
                if (removing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                }
            }
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(top = 20.dp)
        )
    }
}

private fun String?.fromHtml(): String =
    HtmlCompat.fromHtml(orEmpty(), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

@Preview(showBackground = true)
@Composable
private fun CollectArticleRowPreview() {
    val article = ArticleEntity.DatasBean().apply {
        author = "Android Developers"
        title = "Compose 收藏页面迁移"
        niceDate = "2026-11-23"
        superChapterName = "Jetpack Compose"
        desc = "将已有 XML 列表迁移为声明式 UI。"
        projectLink = "https://developer.android.com/"
    }
    CollectScreenTheme(themeColor = Color(0xFF2196F3)) {
        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
            CollectArticleRow(
                article = article,
                removing = false,
                onOpenArticle = {},
                onRemove = {}
            )
        }
    }
}
