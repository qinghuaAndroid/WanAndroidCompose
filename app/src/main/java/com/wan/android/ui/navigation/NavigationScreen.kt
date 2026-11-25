package com.wan.android.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wan.android.R
import com.wan.android.bean.ArticleEntity
import com.wan.android.bean.NavigationEntity
import com.wan.core.ext.getThemeColor
import com.wan.common.R as CommonR
import com.wan.core.R as CoreR

/** 仅映射本页面原有的颜色资源，不影响应用其他 XML 页面的主题。 */
@Composable
internal fun NavigationScreenTheme(
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
            onSurfaceVariant = colorResource(CommonR.color.text_2),
            outlineVariant = colorResource(CommonR.color.division_line)
        ),
        content = content
    )
}

/**
 * 导航页：用 LazyColumn + FlowRow 替代原来的 RecyclerView + LabelsView。
 * 状态由外部传入，预览与测试不需要依赖 ViewModel。
 */
@Composable
internal fun NavigationScreen(
    state: NavigationUiState,
    onOpenArticle: (ArticleEntity.DatasBean) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.items.isNotEmpty() -> NavigationCategoryList(
                categories = state.items,
                onOpenArticle = onOpenArticle
            )

            state.isLoading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )

            else -> NavigationEmptyState(
                message = state.errorMessage ?: stringResource(R.string.navigation_empty),
                onRetry = onRetry
            )
        }
    }
}

@Composable
private fun NavigationCategoryList(
    categories: List<NavigationEntity>,
    onOpenArticle: (ArticleEntity.DatasBean) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = NavigationScreenDefaults.Padding,
            end = NavigationScreenDefaults.Padding,
            bottom = NavigationScreenDefaults.Padding
        )
    ) {
        items(categories) { category ->
            NavigationCategory(category = category, onOpenArticle = onOpenArticle)
        }
    }
}

@Composable
private fun NavigationCategory(
    category: NavigationEntity,
    onOpenArticle: (ArticleEntity.DatasBean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = NavigationScreenDefaults.Padding)
    ) {
        Text(
            text = category.name.orEmpty(),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        // 对应 LabelsView 的 wordMargin = 10dp、lineMargin = 16dp。
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            category.articles.orEmpty().forEach { article ->
                NavigationLabel(
                    text = article.title.orEmpty(),
                    onClick = { onOpenArticle(article) }
                )
            }
        }
    }
}

@Composable
private fun NavigationLabel(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 13.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clip(RoundedCornerShape(NavigationScreenDefaults.LabelCorner))
            .background(colorResource(CommonR.color.search_bg_gray))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun NavigationEmptyState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = NavigationScreenDefaults.Padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
        TextButton(onClick = onRetry) {
            Text(text = stringResource(R.string.navigation_retry))
        }
    }
}

private object NavigationScreenDefaults {
    /** 对应 fragment_system_list.xml 的 padding_left / padding_right。 */
    val Padding = 20.dp
    val LabelCorner = 4.dp
}

@Preview(showBackground = true)
@Composable
private fun NavigationScreenPreview() {
    val categories = listOf(
        NavigationEntity(
            cid = 1,
            name = "常用网站",
            articles = listOf("Google开发者", "Github", "stackoverflow", "掘金", "CSDN").mapIndexed { index, title ->
                ArticleEntity.DatasBean(id = index, title = title, link = "https://www.wanandroid.com")
            }
        ),
        NavigationEntity(
            cid = 2,
            name = "开发必备",
            articles = listOf("maven仓库 阿里云托管", "在线Android源码", "Material Design 中文版")
                .mapIndexed { index, title ->
                    ArticleEntity.DatasBean(id = 100 + index, title = title, link = "https://www.wanandroid.com")
                }
        )
    )
    NavigationScreenTheme(themeColor = colorResource(CoreR.color.Blue)) {
        NavigationScreen(
            state = NavigationUiState(items = categories),
            onOpenArticle = {},
            onRetry = {}
        )
    }
}
