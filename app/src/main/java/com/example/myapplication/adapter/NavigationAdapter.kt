package  com.example.myapplication.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import  com.example.myapplication.R
import  com.example.myapplication.bean.NavigationEntity
import  com.example.myapplication.databinding.ItemNavigationBinding
import  com.example.myapplication.ui.navigation.NavigationPresenter

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