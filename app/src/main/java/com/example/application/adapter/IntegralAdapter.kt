package  com.example.application.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import  com.example.application.R
import  com.example.application.bean.IntegralRecordEntity
import  com.example.application.databinding.ItemIntegralBinding

/**
 * @author cy
 * Create at 2020/4/14.
 */
class IntegralAdapter :
    BaseQuickAdapter<IntegralRecordEntity.DatasBean, BaseDataBindingHolder<ItemIntegralBinding>>(R.layout.item_integral),
    LoadMoreModule {

    override fun convert(
        holder: BaseDataBindingHolder<ItemIntegralBinding>,
        item: IntegralRecordEntity.DatasBean
    ) {
        holder.dataBinding?.let {
            it.data = item
            it.executePendingBindings()
        }
    }

}