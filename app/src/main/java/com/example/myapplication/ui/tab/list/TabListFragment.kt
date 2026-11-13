package  com.example.myapplication.ui.tab.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import  com.example.myapplication.R
import com.example.myapplication.adapter.ArticleAdapter
import  com.example.myapplication.constant.Const
import  com.example.myapplication.databinding.FragmentArticleListBinding
import  com.example.myapplication.ui.ArticleViewModel
import  com.example.myapplication.ui.collect.CollectViewModel
import  com.example.core.ext.collectOnLifecycle
import  com.example.core.ext.showToast
import  com.example.core.mvvm.BaseVMFragment
import  com.example.core.widget.LoadMoreView
import com.example.myapplication.ui.browser.BrowserNormalActivity
import dagger.hilt.android.AndroidEntryPoint


/**
 * @author cy
 * Create at 2020/4/2.
 */
@AndroidEntryPoint
class TabListFragment :
    BaseVMFragment<ArticleViewModel, FragmentArticleListBinding>() {

    private val articleViewModel by viewModels<ArticleViewModel>()
    private val collectViewModel by viewModels<CollectViewModel>()
    private val articleAdapter by lazy { ArticleAdapter() }
    private var projectId: Int = 0
    private var name: String? = null
    private var type: Int? = null
    private var curPosition = 0

    override fun attachLayoutRes(): Int = R.layout.fragment_article_list

    override fun initData(savedInstanceState: Bundle?) {
        type = arguments?.getInt(Const.TYPE)
        projectId = arguments?.getInt(Const.ID) ?: 0
        name = arguments?.getString(Const.NAME) ?: ""
    }

    override fun initView(view: View) {
        initRecyclerView()
        binding.swipeRefresh.setOnRefreshListener { loadData() }
    }

    private fun initRecyclerView() {
        binding.rvTabList.layoutManager = LinearLayoutManager(context)
        binding.rvTabList.adapter = articleAdapter
        articleAdapter.loadMoreModule.setOnLoadMoreListener { loadMore() }
        articleAdapter.loadMoreModule.loadMoreView = LoadMoreView()
        articleAdapter.loadMoreModule.isAutoLoadMore = true
        articleAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = true
        articleAdapter.setOnItemClickListener(mOnItemClickListener)
        articleAdapter.addChildClickViewIds(R.id.ivCollect)
        articleAdapter.setOnItemChildClickListener(mOnItemChildClickListener)
    }

    private val mOnItemClickListener = OnItemClickListener { _, _, position ->
        val datasBean = articleAdapter.data[position]
        startActivity(Intent(requireContext(), BrowserNormalActivity::class.java).apply {
            putExtra(com.example.common.constant.Const.WEB_TITLE, datasBean.title)
            putExtra(com.example.common.constant.Const.WEB_URL, datasBean.link)
        })
    }

    private val mOnItemChildClickListener = OnItemChildClickListener { _, view, position ->
        val datasBean = articleAdapter.data[position]
        curPosition = position
        when (view.id) {
            R.id.ivCollect -> {
                if (datasBean.collect) collectViewModel.unCollect(datasBean.id)
                else collectViewModel.collect(datasBean.id)
            }
        }
    }

    override fun loadData() {
        type?.let {
            if (type == Const.PROJECT_TYPE)
                articleViewModel.getProjectList(true, projectId)
            else articleViewModel.getAccountList(true, projectId)
        }

    }

    private fun loadMore() {
        type?.let {
            if (type == Const.PROJECT_TYPE)
                articleViewModel.getProjectList(false, projectId)
            else articleViewModel.getAccountList(false, projectId)
        }
    }

    override fun subscribeUi() {
        articleViewModel.uiState.collectOnLifecycle(viewLifecycleOwner) {
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
        collectViewModel.uiState.collectOnLifecycle(viewLifecycleOwner) {
            if (it.showLoading) showProgressDialog() else dismissProgressDialog()
            it.showSuccess?.let { collect ->
                articleAdapter.data[curPosition].collect = collect
                articleAdapter.notifyItemChanged(curPosition)
            }
            it.showError?.let { errorMsg -> showToast(errorMsg) }
        }
    }

}