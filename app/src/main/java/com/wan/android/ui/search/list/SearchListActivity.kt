package  com.wan.android.ui.search.list

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import com.chad.library.adapter.base.listener.OnItemClickListener
import  com.wan.android.R
import com.wan.android.adapter.ArticleAdapter
import  com.wan.android.constant.Const
import  com.wan.android.databinding.ActivitySystemBinding
import  com.wan.android.ui.ArticleViewModel
import com.wan.android.ui.browser.BrowserNormalActivity
import  com.wan.core.ext.collectOnLifecycle
import  com.wan.core.ext.showToast
import  com.wan.core.mvvm.BaseVMActivity
import  com.wan.core.widget.LoadMoreView
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author cy
 * Create at 2020/4/8.
 */
@AndroidEntryPoint
class SearchListActivity : BaseVMActivity<ArticleViewModel, ActivitySystemBinding>() {

    private val articleViewModel by viewModels<ArticleViewModel>()
    private val articleAdapter by lazy { ArticleAdapter() }

    private val key: String by lazy { intent.getStringExtra(Const.SEARCH_KEY).orEmpty() }

    override fun subscribeUi() {
        articleViewModel.uiState.collectOnLifecycle(this) {
            binding.swipeRefresh.isRefreshing = it.showLoading
            it.showSuccess?.let { articleEntity ->
                articleEntity.datas?.let { list ->
                    if (it.isRefresh) articleAdapter.setList(list)
                    else articleAdapter.addData(list)
                }
                articleAdapter.loadMoreModule.loadMoreComplete()
            }
            it.showError?.let { errorMsg ->
                showToast(errorMsg)
                articleAdapter.loadMoreModule.loadMoreFail()
            }
            if (it.showEnd) articleAdapter.loadMoreModule.loadMoreEnd()
            articleAdapter.loadMoreModule.isEnableLoadMore = it.isEnableLoadMore
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_system

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun initView() {
        title = key
        initRecyclerView()
        binding.swipeRefresh.setOnRefreshListener { loadData() }
    }

    private fun initRecyclerView() {
        binding.rvSystem.layoutManager = LinearLayoutManager(this)
        binding.rvSystem.adapter = articleAdapter
        articleAdapter.loadMoreModule.setOnLoadMoreListener { loadMore() }
        articleAdapter.loadMoreModule.loadMoreView = LoadMoreView()
        articleAdapter.loadMoreModule.isAutoLoadMore = true
        articleAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        articleAdapter.setOnItemClickListener(mOnItemClickListener)
    }

    override fun loadData() {
        articleViewModel.queryBySearchKey(true, key)
    }

    private fun loadMore() {
        articleViewModel.queryBySearchKey(false, key)
    }

    private val mOnItemClickListener = OnItemClickListener { _, _, position ->
        val datasBean = articleAdapter.data[position]
        startActivity(Intent(this, BrowserNormalActivity::class.java).apply {
            putExtra(com.wan.common.constant.Const.WEB_TITLE, datasBean.title)
            putExtra(com.wan.common.constant.Const.WEB_URL, datasBean.link)
        })
    }

}
