package  com.example.application.ui.collect

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import  com.example.application.R
import  com.example.application.adapter.CollectAdapter
import  com.example.application.databinding.ActivityCollectBinding
import  com.example.application.ui.ArticleViewModel
import  com.example.core.ext.collectOnLifecycle
import  com.example.core.ext.showToast
import  com.example.core.mvvm.BaseVMActivity
import  com.example.core.widget.LoadMoreView
import com.example.common.constant.Const
import com.example.application.ui.browser.BrowserNormalActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author cy
 * Create at 2020/4/13.
 */
@AndroidEntryPoint
class CollectActivity :
    BaseVMActivity<ArticleViewModel, ActivityCollectBinding>() {
    private val articleViewModel by viewModels<ArticleViewModel>()
    private val collectViewModel by viewModels<CollectViewModel>()
    private val collectAdapter by lazy { CollectAdapter() }
    private var curPosition = 0

    override fun getLayoutId(): Int = R.layout.activity_collect

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView() {
        title = "我的收藏"
        initRecyclerView()
        binding.swipeRefresh.setOnRefreshListener { loadData() }
    }

    private fun initRecyclerView() {
        binding.rvCollect.layoutManager = LinearLayoutManager(this)
        binding.rvCollect.adapter = collectAdapter
        collectAdapter.run {
            loadMoreModule.setOnLoadMoreListener { loadMore() }
            loadMoreModule.loadMoreView = LoadMoreView()
            loadMoreModule.isAutoLoadMore = true
            loadMoreModule.isEnableLoadMoreIfNotFullPage = true
            setOnItemClickListener(mOnItemClickListener)
            addChildClickViewIds(R.id.ivCollect)
            setOnItemChildClickListener(mOnItemChildClickListener)
        }
    }

    private val mOnItemClickListener = OnItemClickListener { _, _, position ->
        val datasBean = collectAdapter.data[position]
        startActivity(Intent(this, BrowserNormalActivity::class.java).apply {
            putExtra(Const.WEB_TITLE, datasBean.title)
            putExtra(Const.WEB_URL, datasBean.link)
        })
    }

    private val mOnItemChildClickListener = OnItemChildClickListener { _, view, position ->
        val datasBean = collectAdapter.data[position]
        curPosition = position
        when (view.id) {
            R.id.ivCollect -> {
                collectViewModel.unMyCollect(datasBean.id, datasBean.originId)
            }
        }
    }

    override fun loadData() {
        articleViewModel.getCollectData(true)
    }

    private fun loadMore() {
        articleViewModel.getCollectData(false)
    }

    override fun subscribeUi() {
        articleViewModel.uiState.collectOnLifecycle(this) {
            binding.swipeRefresh.isRefreshing = it.showLoading
            it.showSuccess?.let { articleEntity ->
                articleEntity.datas?.let { list ->
                    if (it.isRefresh) collectAdapter.setList(list)
                    else collectAdapter.addData(list)
                }
                collectAdapter.loadMoreModule.loadMoreComplete()
            }
            it.showError?.let { errorMsg ->
                showToast(errorMsg)
                collectAdapter.loadMoreModule.loadMoreFail()
            }
            if (it.showEnd) collectAdapter.loadMoreModule.loadMoreEnd()
            collectAdapter.loadMoreModule.isEnableLoadMore = it.isEnableLoadMore
        }
        collectViewModel.uiState.collectOnLifecycle(this) {
            if (it.showLoading) showProgressDialog() else dismissProgressDialog()
            it.showSuccess?.let { collect ->
                if (collect.not()) {
                    collectAdapter.removeAt(curPosition)
                }
            }
            it.showError?.let { errorMsg -> showToast(errorMsg) }
        }
    }
}