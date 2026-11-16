package com.example.application.ui.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.application.bean.TabEntity
import com.example.application.constant.Const
import com.example.core.ext.getThemeColor
import com.example.core.ext.showToast
import com.example.core.flowbus.SharedFlowBus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * Compose 标签页容器；文章列表暂时复用现有的 TabListFragment。
 */
@AndroidEntryPoint
class TabFragment : Fragment() {

    private val tabViewModel by viewModels<TabViewModel>()
    private val tabs = MutableStateFlow<List<TabEntity>>(emptyList())
    private val themeColor = MutableStateFlow(0)
    private val type: Int? get() = arguments?.getInt(Const.TYPE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        // 使用固定 ID，让 rememberSaveable 的分页状态可以随 View 恢复。
        id = com.example.application.R.id.tab_compose_view
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        themeColor.value = getThemeColor()
        setContent {
            val tabList = tabs.collectAsStateWithLifecycle().value
            val color = themeColor.collectAsStateWithLifecycle().value
            TabScreenTheme(themeColor = Color(color)) {
                TabScreen(tabs = tabList) { tab, selected ->
                    TabListPage(tab = tab, type = type, selected = selected)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // uiState 没有 replay，先订阅再发请求，避免快速响应丢失。
        viewLifecycleOwner.lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                tabViewModel.uiState.collect { state ->
                    state.showSuccess?.let { tabs.value = it.toList() }
                    state.showError?.let { showToast(it) }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                SharedFlowBus.with<Int>(com.example.common.constant.Const.THEME_COLOR)
                    .collect { themeColor.value = getThemeColor() }
            }
        }
        if (tabs.value.isEmpty()) {
            type?.let { tabViewModel.getTabList(it) }
        }
    }
}
