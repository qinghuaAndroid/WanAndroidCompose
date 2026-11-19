package com.example.application.ui.tab

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.compose.AndroidFragment
import androidx.lifecycle.Lifecycle
import com.example.application.bean.TabEntity
import com.example.application.constant.Const
import com.example.application.ui.tab.list.TabListFragment
import kotlinx.coroutines.launch
import com.example.common.R as CommonR
import com.example.core.R as CoreR

/** 仅映射本页面原有的颜色资源，不影响应用其他 XML 页面的主题。 */
@Composable
internal fun TabScreenTheme(themeColor: Color, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = themeColor,
            onPrimary = colorResource(CommonR.color.tab_text_color),
            background = colorResource(CommonR.color.viewBackground),
            surface = colorResource(CommonR.color.viewBackground),
            outlineVariant = colorResource(CommonR.color.division_line_thickening),
        ),
        content = content
    )
}

/**
 * 用可滚动 TabRow + HorizontalPager 替代 TabLayout + ViewPager2。
 * pageContent 与容器分离，预览和测试不需要创建真正的 Fragment。
 */
@Composable
internal fun TabScreen(
    tabs: List<TabEntity>,
    modifier: Modifier = Modifier,
    pageContent: @Composable (tab: TabEntity, selected: Boolean) -> Unit
) {
    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (tabs.isEmpty()) {
            Box(
                Modifier.fillMaxWidth().height(TabScreenDefaults.TabHeight)
                    .background(MaterialTheme.colorScheme.primary)
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            return@Column
        }
        val pagerState = rememberPagerState { tabs.size }
        val scope = rememberCoroutineScope()
        PrimaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth().height(TabScreenDefaults.TabHeight),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            edgePadding = 0.dp,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        selectedTabIndex = pagerState.currentPage,
                        matchContentSize = true
                    ),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    selectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedContentColor = MaterialTheme.colorScheme.onPrimary,
                    text = {
                        Text(tab.name.orEmpty(), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f),
            key = { tabs[it].id }
        ) { page ->
            pageContent(tabs[page], pagerState.settledPage == page)
        }
    }
}

/** 使用 Fragment 保持现有 Hilt ViewModel 与分页生命周期边界，页面内容由 Compose 渲染。 */
@Composable
internal fun TabListPage(tab: TabEntity, type: Int?, selected: Boolean) {
    val arguments = remember(tab.id, tab.name, type) {
        Bundle().apply {
            type?.let { putInt(Const.TYPE, it) }
            putInt(Const.ID, tab.id)
            putString(Const.NAME, tab.name)
        }
    }
    AndroidFragment<TabListFragment>(
        modifier = Modifier.fillMaxSize(),
        arguments = arguments,
        maxLifecycle = if (selected) Lifecycle.State.RESUMED else Lifecycle.State.STARTED
    )
}

private object TabScreenDefaults {
    // 对应 fragment_tab.xml 的 50dp 高度。
    val TabHeight = 50.dp
}

@Preview(showBackground = true)
@Composable
private fun TabScreenPreview() {
    val tabs = listOf("完整项目", "跨平台应用", "开发工具").mapIndexed { index, title ->
        TabEntity().apply { id = index; name = title }
    }
    TabScreenTheme(themeColor = colorResource(CoreR.color.Blue)) {
        TabScreen(tabs = tabs) { _, _ -> Box(Modifier.fillMaxSize()) }
    }
}
