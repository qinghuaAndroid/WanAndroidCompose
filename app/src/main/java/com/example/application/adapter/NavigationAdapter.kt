package  com.example.application.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import  com.example.application.R
import  com.example.application.bean.NavigationEntity
import  com.example.application.databinding.ItemNavigationBinding
import  com.example.application.ui.navigation.NavigationPresenter

/**
 * @author cy
 * Create at 2020/4/8.
 */
class NavigationAdapter :
    BaseQuickAdapter<NavigationEntity,
            BaseDataBindingHolder<ItemNavigationBinding>>(R.layout.item_navigation), LoadMoreModule {

    private val presenter: NavigationPresenter by lazy { NavigationPresenter() }

    override fun convert(holder: BaseDataBindingHolder<ItemNavigationBinding>, item: NavigationEntity) {
        holder.dataBinding?.let {
            it.navigationEntity = item
            it.presenter = presenter
            it.executePendingBindings()
        }
    }
}