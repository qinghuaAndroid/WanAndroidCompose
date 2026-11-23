package  com.example.application.ui.system.act

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import com.chad.library.adapter.base.listener.OnItemClickListener
import  com.example.application.R
import com.example.application.adapter.ArticleAdapter
import  com.example.application.constant.Const
import  com.example.application.databinding.ActivitySystemBinding
import  com.example.application.ui.ArticleViewModel
import com.example.application.ui.browser.BrowserNormalActivity
import  com.example.core.ext.collectOnLifecycle
import  com.example.core.ext.showToast
import  com.example.core.mvvm.BaseVMActivity
import  com.example.core.widget.LoadMoreView
import dagger.hilt.android.AndroidEntryPoint


/**
 * @author cy
 * Create at 2020/4/8.
 */
@AndroidEntryPoint
class SystemActivity : BaseVMActivity<ArticleViewModel, ActivitySystemBinding>() {

    private val articleViewModel by viewModels<ArticleViewModel>()
    private val articleAdapter by lazy { ArticleAdapter() }

    private val cid: Int by lazy { intent.getIntExtra(Const.SYSTEM_ID, 0) }
    private val systemTitle: String? by lazy { intent.getStringExtra(Const.SYSTEM_TITLE) }

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
        systemTitle?.let { setTitle(it) }
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
        articleViewModel.getSystemArticle(true, cid)
    }

    private fun loadMore() {
        articleViewModel.getSystemArticle(false, cid)
    }

    private val mOnItemClickListener = OnItemClickListener { _, _, position ->
        val datasBean = articleAdapter.data[position]
        startActivity(Intent(this, BrowserNormalActivity::class.java).apply {
            putExtra(com.example.common.constant.Const.WEB_TITLE, datasBean.title)
            putExtra(com.example.common.constant.Const.WEB_URL, datasBean.link)
        })
    }

}
