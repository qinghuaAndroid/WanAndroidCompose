package  com.example.application.ui.navigation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import  com.example.application.R
import  com.example.application.adapter.NavigationAdapter
import  com.example.application.databinding.FragmentSystemListBinding
import  com.example.core.ext.collectOnLifecycle
import  com.example.core.ext.showToast
import  com.example.core.mvvm.BaseVMFragment
import dagger.hilt.android.AndroidEntryPoint


/**
 * @author cy
 * Create at 2020/4/8.
 */
@AndroidEntryPoint
class NavigationFragment : BaseVMFragment<NavigationViewModel, FragmentSystemListBinding>() {

    private val mViewModel by viewModels<NavigationViewModel>()

    private val navigationAdapter by lazy { NavigationAdapter() }

    override fun attachLayoutRes(): Int = R.layout.fragment_system_list

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView(view: View) {
        binding.rvSystem.layoutManager = LinearLayoutManager(context)
        binding.rvSystem.adapter = navigationAdapter
    }

    override fun loadData() {
        mViewModel.getNavigation()
    }

    override fun subscribeUi() {
        mViewModel.uiState.collectOnLifecycle(viewLifecycleOwner) { baseUiState ->
            baseUiState.showSuccess?.let { navigationAdapter.setList(it) }
            baseUiState.showError?.let { showToast(it) }
        }
    }
}