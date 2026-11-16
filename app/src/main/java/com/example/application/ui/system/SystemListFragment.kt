package  com.example.application.ui.system

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kingja.loadsir.core.LoadService
import com.kingja.loadsir.core.LoadSir
import  com.example.application.R
import  com.example.application.adapter.SystemListAdapter
import  com.example.application.databinding.FragmentSystemListBinding
import  com.example.core.ext.collectOnLifecycle
import  com.example.core.ext.showToast
import  com.example.core.mvvm.BaseVMFragment
import  com.example.common.callback.EmptyCallback
import  com.example.common.callback.ErrorCallback
import  com.example.common.callback.LoadingCallback
import dagger.hilt.android.AndroidEntryPoint


/**
 * @author cy
 * Create at 2020/4/8.
 */
@AndroidEntryPoint
class SystemListFragment : BaseVMFragment<SystemListViewModel, FragmentSystemListBinding>() {

    private val mViewModel by viewModels<SystemListViewModel>()

    private val systemListAdapter by lazy { SystemListAdapter() }

    private lateinit var loadService: LoadService<Any>

    override fun attachLayoutRes(): Int = R.layout.fragment_system_list

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView(view: View) {
        //第二步：注册布局View
        loadService = LoadSir.getDefault().register(binding.rvSystem) { loadData() }
        binding.rvSystem.layoutManager = LinearLayoutManager(context)
        binding.rvSystem.adapter = systemListAdapter
    }

    override fun loadData() {
        mViewModel.getSystemList()
    }

    override fun subscribeUi() {
        loadService.showCallback(LoadingCallback::class.java)
        mViewModel.uiState.collectOnLifecycle(viewLifecycleOwner) { baseUiState ->
            baseUiState.showSuccess?.let {
                if (it.isEmpty()) {
                    loadService.showCallback(EmptyCallback::class.java)
                } else {
                    loadService.showSuccess()
                    systemListAdapter.setList(it)
                }
            }
            baseUiState.showError?.let {
                loadService.showCallback(ErrorCallback::class.java)
                showToast(it)
            }
        }
    }
}