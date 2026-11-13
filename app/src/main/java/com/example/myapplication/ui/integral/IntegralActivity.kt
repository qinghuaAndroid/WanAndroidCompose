package  com.example.myapplication.ui.integral

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import  com.example.myapplication.R
import  com.example.myapplication.adapter.IntegralAdapter
import  com.example.myapplication.bean.CoinInfo
import  com.example.myapplication.databinding.ActivityIntegralBinding
import  com.example.myapplication.ui.main.MainViewModel
import  com.example.core.ext.collectOnLifecycle
import  com.example.core.ext.showToast
import  com.example.core.mvvm.BaseVMActivity
import  com.example.core.widget.LoadMoreView
import dagger.hilt.android.AndroidEntryPoint


/**
 * @author cy
 * Create at 2020/4/10.
 */
@AndroidEntryPoint
class IntegralActivity : BaseVMActivity<IntegralViewModel, ActivityIntegralBinding>() {

    private val integralViewModel by viewModels<IntegralViewModel>()
    private val mainViewModel by viewModels<MainViewModel>()
    private val integralAdapter by lazy { IntegralAdapter() }
    private lateinit var headerView: View

    override fun subscribeUi() {
        mainViewModel.uiState.collectOnLifecycle(this) {
            it.showSuccess?.let { userInfoEntity ->
                userInfoEntity.coinInfo?.let { coinInfo ->
                    startAnim(coinInfo)
                }
            }
        }

        integralViewModel.uiState.collectOnLifecycle(this) {
            binding.swipeRefresh.isRefreshing = it.showLoading
            it.showSuccess?.let { articleEntity ->
                articleEntity.datas?.let { list ->
                    if (it.isRefresh) integralAdapter.setList(list)
                    else integralAdapter.addData(list)
                }
                integralAdapter.loadMoreModule.loadMoreComplete()
            }
            it.showError?.let { errorMsg ->
                showToast(errorMsg)
                integralAdapter.loadMoreModule.loadMoreFail()
            }
            if (it.showEnd) integralAdapter.loadMoreModule.loadMoreEnd()
            integralAdapter.loadMoreModule.isEnableLoadMore = it.isEnableLoadMore
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_integral

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun initView() {
        title = getString(R.string.my_integral)
        initRecyclerView()
        binding.swipeRefresh.setOnRefreshListener { loadData() }
        binding.appBar.addOnOffsetChangedListener { _, verticalOffset ->
            binding.swipeRefresh.isEnabled = verticalOffset >= 0
        }
    }

    /**
     * 开启积分动画
     */
    private fun startAnim(coinInfo: CoinInfo) {
        val animator = ValueAnimator.ofInt(0, coinInfo.coinCount)
        //播放时长
        animator.duration = 1500
        animator.interpolator = DecelerateInterpolator()
        animator.addUpdateListener { animation ->
            //获取改变后的值
            val currentValue = animation.animatedValue as Int
            binding.tvIntegralAnim.text = "$currentValue"
        }
        animator.start()
    }

    private fun initRecyclerView() {
        binding.rvIntegralList.run {
            layoutManager = LinearLayoutManager(this@IntegralActivity)
            adapter = integralAdapter
            headerView = layoutInflater.inflate(R.layout.header_integral, this, false)
        }
        integralAdapter.run {
            addHeaderView(headerView)
            loadMoreModule.loadMoreView = LoadMoreView()
            loadMoreModule.setOnLoadMoreListener { loadMore() }
            loadMoreModule.isAutoLoadMore = true
            loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        }
    }

    override fun loadData() {
        mainViewModel.getUserInfo()
        integralViewModel.getIntegralRecord(true)
    }

    private fun loadMore() {
        integralViewModel.getIntegralRecord(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.tvIntegralAnim.clearAnimation()
    }
}