package com.wan.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.Insets
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.trello.rxlifecycle4.components.support.RxFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 * Created by Cy on 2018/9/27.
 */
abstract class BaseFragment<B : ViewDataBinding> : RxFragment(), CoroutineScope by MainScope() {
    private var _binding: B? = null
    protected val binding get() = _binding!!
    private var pendingInsets: Insets? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(savedInstanceState)
        loadData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (attachLayoutRes() == 0) {
            throw RuntimeException("Please set the page layout")
        }
        _binding = DataBindingUtil.inflate(inflater, attachLayoutRes(), container, false)
        _binding?.lifecycleOwner = viewLifecycleOwner //xml中若有使用livedata
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        subscribeEvent()
        pendingInsets?.let {
            pendingInsets = null
            dispatchWindowInsets(it)
        }
    }

    protected abstract fun attachLayoutRes(): Int
    protected abstract fun initData(savedInstanceState: Bundle?)
    protected abstract fun initView(view: View)
    protected abstract fun loadData()
    protected open fun subscribeEvent() {}

    /**
     * 安全区域分发。Activity 已消费 WindowInsets，这里收到的是状态栏、导航栏与软键盘合并后的最大值。
     * 子类按需覆盖，避免内容被系统栏遮挡。
     */
    fun dispatchWindowInsets(insets: Insets) {
        if (_binding == null) {
            pendingInsets = insets
        } else {
            onApplySafeArea(insets)
        }
        dispatchChildWindowInsets(insets)
    }

    /**
     * 当前页面应用安全区域。覆盖时不需要再调用 super。
     */
    protected open fun onApplySafeArea(insets: Insets) {}

    private fun dispatchChildWindowInsets(insets: Insets) {
        childFragmentManager.fragments.forEach { dispatchTo(it, insets) }
    }

    private fun dispatchTo(fragment: Fragment, insets: Insets) {
        if (fragment is BaseFragment<*>) {
            fragment.dispatchWindowInsets(insets)
        } else {
            fragment.childFragmentManager.fragments.forEach { dispatchTo(it, insets) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding?.unbind()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}