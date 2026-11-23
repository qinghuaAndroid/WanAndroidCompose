package  com.wan.android.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.listener.OnItemClickListener
import  com.wan.android.R
import com.wan.android.adapter.ArticleAdapter
import  com.wan.android.adapter.ImageNetAdapter
import  com.wan.android.databinding.FragmentHomeBinding
import  com.wan.android.ui.ArticleViewModel
import  com.wan.core.ext.collectOnLifecycle
import  com.wan.core.ext.showToast
import  com.wan.core.mvvm.BaseVMFragment
import  com.wan.core.widget.LoadMoreView
import com.wan.common.constant.Const
import com.wan.android.ui.browser.BrowserNormalActivity
import dagger.hilt.android.AndroidEntryPoint


/**
 * @author cy
 * Create at 2020/4/2.
 */
@AndroidEntryPoint
class HomeFragment : BaseVMFragment<ArticleViewModel, FragmentHomeBinding>() {

    private val homeViewModel by viewModels<HomeViewModel>()
    private val articleViewModel by viewModels<ArticleViewModel>()
    private val articleAdapter by lazy { ArticleAdapter() }

    override fun attachLayoutRes(): Int = R.layout.fragment_home

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView(view: View) {
        initRecyclerView()
        binding.swipeRefresh.setOnRefreshListener { loadData() }
        binding.appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            binding.swipeRefresh.isEnabled = verticalOffset >= 0
        }
    }

    private fun initRecyclerView() {
        binding.rvHomeList.layoutManager = LinearLayoutManager(context)
        binding.rvHomeList.adapter = articleAdapter
        articleAdapter.loadMoreModule.loadMoreView = LoadMoreView()
        articleAdapter.loadMoreModule.setOnLoadMoreListener { loadMore() }
        articleAdapter.loadMoreModule.isAutoLoadMore = true
        articleAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = true
        articleAdapter.setOnItemClickListener(mOnItemClickListener)
    }

    private val mOnItemClickListener = OnItemClickListener { _, _, position ->
        val datasBean = articleAdapter.data[position]
        startActivity(Intent(requireContext(), BrowserNormalActivity::class.java).apply {
            putExtra(Const.WEB_TITLE, datasBean.title)
            putExtra(Const.WEB_URL, datasBean.link)
        })
    }

    override fun loadData() {
        homeViewModel.getBanner()
        homeViewModel.getTopArticles()
        articleViewModel.getArticles(true)
    }

    private fun loadMore() {
        articleViewModel.getArticles(false)
    }

    override fun onStart() {
        super.onStart()
        binding.banner.start()
    }

    override fun onStop() {
        super.onStop()
        binding.banner.stop()
    }

    override fun subscribeUi() {
        homeViewModel.bannerUiState.collectOnLifecycle(viewLifecycleOwner) {
            it.showSuccess?.let { list ->
                binding.banner.setAdapter(ImageNetAdapter(list))
            }
            it.showError?.let { errorMsg -> showToast(errorMsg) }
        }
        homeViewModel.topArticleUiState.collectOnLifecycle(viewLifecycleOwner) {
            it.showSuccess?.let { list ->
                articleAdapter.setList(list)
            }
            it.showError?.let { errorMsg -> showToast(errorMsg) }
        }
        articleViewModel.uiState.collectOnLifecycle(viewLifecycleOwner) {
            binding.swipeRefresh.isRefreshing = it.showLoading
            it.showSuccess?.let { articleEntity ->
                articleEntity.datas?.let { list ->
                    if (it.isRefresh) articleAdapter.addData(list)
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
}
