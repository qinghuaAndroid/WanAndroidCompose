package  com.wan.android.ui.system

import android.content.Intent
import android.view.View
import  com.wan.android.bean.SystemListEntity
import  com.wan.android.constant.Const
import com.wan.android.ui.system.act.SystemActivity

/**
 * @author cy
 * Create at 2020/5/9.
 */
class SystemListPresenter {

    val clickLabel: (View, SystemListEntity.ChildrenBean) -> Unit = { view, childrenBean -> clickLabel(view, childrenBean) }

    private fun clickLabel(view: View, childrenBean: SystemListEntity.ChildrenBean) {
        val context = view.context ?: return
        val id = childrenBean.id
        val title = childrenBean.name
        context.startActivity(Intent(context, SystemActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Const.SYSTEM_ID, id)
            putExtra(Const.SYSTEM_TITLE, title)
        })
    }
}