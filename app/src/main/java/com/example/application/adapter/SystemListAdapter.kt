package  com.example.application.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import  com.example.application.R
import  com.example.application.bean.SystemListEntity
import  com.example.application.databinding.ItemSystemBinding
import  com.example.application.ui.system.SystemListPresenter

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