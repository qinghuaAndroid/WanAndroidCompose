package  com.example.application.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import  com.example.application.R
import  com.example.application.bean.HotSearchEntity
import  com.example.application.constant.Const
import  com.example.application.databinding.ActivitySearchBinding
import  com.example.core.ext.collectOnLifecycle
import  com.example.core.ext.listener.queryTextListener
import  com.example.core.ext.showToast
import  com.example.core.mvvm.BaseVMActivity
import com.example.application.ui.search.list.SearchListActivity
import dagger.hilt.android.AndroidEntryPoint
import splitties.views.onClick

/**
 * @author cy
 * Create at 2020/4/14.
 */
@AndroidEntryPoint
class SearchActivity : BaseVMActivity<SearchViewModel, ActivitySearchBinding>() {

    private val searchViewModel by viewModels<SearchViewModel>()

    override fun getLayoutId(): Int = R.layout.activity_search

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView() {
        setTitle(R.string.search)
        initListener()
    }

    private fun initListener() {
        binding.ivClearAll.onClick { searchViewModel.clearAll() }
    }

    private fun goToSearchList(key: String) {
        searchViewModel.saveSearchKey(key)
        startActivity(Intent(this, SearchListActivity::class.java).apply {
            putExtra(Const.SEARCH_KEY, key)
        })
    }

    override fun loadData() {
        searchViewModel.getHotSearchData()
        searchViewModel.queryAll()
    }

    override fun subscribeUi() {
        searchViewModel.uiState.collectOnLifecycle(this) {
            if (it.showLoading) showProgressDialog() else dismissProgressDialog()
            it.showSuccess?.let { historyBeans -> showHotSearchData(historyBeans) }
            it.showError?.let { errorMsg -> showToast(errorMsg) }
        }
        searchViewModel.historyList.collectOnLifecycle(this) { list ->
            showHistoryData(list)
        }
    }

    private fun showHotSearchData(hotSearchDatas: MutableList<HotSearchEntity>) {
        binding.hotLabelsView.run {
            setLabels(hotSearchDatas) { _, _, data ->
                data.name
            }
            setOnLabelClickListener { _, _, position ->
                onLabelClick(hotSearchDatas[position].name)
            }
        }
    }

    private fun showHistoryData(histories: List<String>) {
        binding.historyLabelsView.run {
            setLabels(histories) { _, _, data ->
                data
            }
            setOnLabelClickListener { _, _, position ->
                onLabelClick(histories[position])
            }
            setOnLabelLongClickListener { _, _, position ->
                onLabelLongClick(histories[position])
                true
            }
        }
    }

    private fun onLabelLongClick(key: String) {
        searchViewModel.deleteByKey(key)
    }

    private fun onLabelClick(queryTxt: String) {
        goToSearchList(queryTxt)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.onActionViewExpanded()
        searchView.queryHint = getString(R.string.search_tint)
        searchView.queryTextListener {
            onQueryTextSubmit { query ->
                query?.let { goToSearchList(it) }
            }
        }
        searchView.isSubmitButtonEnabled = true
        initStyle(searchView)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initStyle(searchView: SearchView) {
        val goButton = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_go_btn)
        goButton.setImageResource(R.drawable.ic_search_white_24dp)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}