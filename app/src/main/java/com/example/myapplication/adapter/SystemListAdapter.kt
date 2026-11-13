package  com.example.myapplication.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import  com.example.myapplication.R
import  com.example.myapplication.bean.SystemListEntity
import  com.example.myapplication.databinding.ItemSystemBinding
import  com.example.myapplication.ui.system.SystemListPresenter

/**
 * @author cy
 * Create at 2020/4/8.
 */
class SystemListAdapter :
    BaseQuickAdapter<SystemListEntity,
            BaseDataBindingHolder<ItemSystemBinding>>(R.layout.item_system), LoadMoreModule {

    private val presenter: SystemListPresenter by lazy { SystemListPresenter() }

    override fun convert(holder: BaseDataBindingHolder<ItemSystemBinding>, item: SystemListEntity) {
        holder.dataBinding?.let {
            it.systemListEntity = item
            it.presenter = presenter
            it.executePendingBindings()
        }
    }
}