package  com.example.application.ui.myarticle

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnItemClickListener
import  com.example.application.R
import com.example.application.adapter.ArticleAdapter
import  com.example.application.databinding.ActivityMyArticleBinding
import  com.example.core.ext.collectOnLifecycle
import  com.example.core.ext.showToast
import  com.example.core.mvvm.BaseVMActivity
import  com.example.core.widget.LoadMoreView
import com.example.common.constant.Const
import com.example.application.ui.browser.BrowserNormalActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * @author cy
 * Create at 2020/4/15.
 */
@AndroidEntryPoint
class MyArticleActivity : BaseVMActivity<MyArticleViewModel, ActivityMyArticleBinding>() {

    private val articleViewModel by viewModels<MyArticleViewModel>()
    private val articleAdapter by lazy { ArticleAdapter() }

    override fun subscribeUi() {
        articleViewModel.uiState.collectOnLifecycle(this) {
            binding.swipeRefresh.isRefreshing = it.showLoading
            it.showSuccess?.let { myArticleEntity ->
                myArticleEntity.shareArticles?.datas?.let { list ->
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

    override fun getLayoutId(): Int = R.layout.activity_my_article

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView() {
        title = getString(R.string.my_article)
        initRecyclerView()
        binding.swipeRefresh.setOnRefreshListener { loadData() }
    }

    private fun initRecyclerView() {
        binding.rvMyArticle.run {
            layoutManager = LinearLayoutManager(this@MyArticleActivity)
            adapter = articleAdapter
        }
        articleAdapter.run {
            loadMoreModule.loadMoreView = LoadMoreView()
            loadMoreModule.setOnLoadMoreListener { loadMore() }
            loadMoreModule.isAutoLoadMore = true
            loadMoreModule.isEnableLoadMoreIfNotFullPage = false
            setOnItemClickListener(mOnItemClickListener)
        }
    }

    private val mOnItemClickListener = OnItemClickListener { _, _, position ->
        val datasBean = articleAdapter.data[position]
        startActivity(Intent(this, BrowserNormalActivity::class.java).apply {
            putExtra(Const.WEB_TITLE, datasBean.title)
            putExtra(Const.WEB_URL, datasBean.link)
        })
    }

    override fun loadData() {
        articleViewModel.getMyArticle(true)
    }

    private fun loadMore() {
        articleViewModel.getMyArticle(false)
    }
}
