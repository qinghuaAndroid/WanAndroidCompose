package  com.example.application.ui.tab

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.google.android.material.tabs.TabLayoutMediator
import  com.example.application.R
import  com.example.application.adapter.TabPagerAdapter
import  com.example.application.bean.TabEntity
import  com.example.application.constant.Const
import  com.example.application.databinding.FragmentTabBinding
import  com.example.core.ext.collectOnLifecycle
import  com.example.core.ext.getThemeColor
import  com.example.core.ext.showToast
import  com.example.core.flowbus.SharedFlowBus
import  com.example.core.mvvm.BaseVMFragment
import dagger.hilt.android.AndroidEntryPoint
import splitties.views.backgroundColor

/**
 * @author cy
 * Create at 2020/4/2.
 */
@AndroidEntryPoint
class TabFragment : BaseVMFragment<TabViewModel, FragmentTabBinding>() {

    private val tabViewModel by viewModels<TabViewModel>()

    private var type: Int? = null

    override fun attachLayoutRes(): Int = R.layout.fragment_tab

    override fun initData(savedInstanceState: Bundle?) {
        type = arguments?.getInt(Const.TYPE)
    }

    override fun initView(view: View) {
        setThemeColor()
    }

    override fun loadData() {
        type?.let { tabViewModel.getTabList(it) }
    }

    override fun subscribeEvent() {
        SharedFlowBus.with<Int>(com.example.common.constant.Const.THEME_COLOR)
            .collectOnLifecycle(viewLifecycleOwner, Lifecycle.State.CREATED) {
                setThemeColor()
            }
    }

    private fun setThemeColor() {
        binding.tabLayout.backgroundColor = getThemeColor()
    }

    override fun subscribeUi() {
        tabViewModel.uiState.collectOnLifecycle(viewLifecycleOwner) {
            if (it.showLoading) showProgressDialog() else dismissProgressDialog()
            it.showSuccess?.let { tabList -> showList(tabList) }
            it.showError?.let { errorMsg -> showToast(errorMsg) }
        }
    }

    private fun showList(list: MutableList<TabEntity>) {
        val titleList = mutableListOf<String>()
        list.forEach {
            it.name?.let { it1 -> titleList.add(it1) }
        }
        binding.viewPager.adapter = TabPagerAdapter(this, list, type)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titleList[position]
        }.attach()
    }
}