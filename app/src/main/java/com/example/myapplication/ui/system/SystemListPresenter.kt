package  com.example.myapplication.ui.system

import android.content.Intent
import com.example.core.app.App
import  com.example.myapplication.bean.SystemListEntity
import  com.example.myapplication.constant.Const
import com.example.myapplication.ui.system.act.SystemActivity

/**
 * @author cy
 * Create at 2020/5/9.
 */
class SystemListPresenter {

    val clickLabel: (SystemListEntity.ChildrenBean) -> Unit = { clickLabel(it) }

    private fun clickLabel(childrenBean: SystemListEntity.ChildrenBean) {
        val id = childrenBean.id
        val title = childrenBean.name
        App.context().startActivity(Intent(App.context(), SystemActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Const.SYSTEM_ID, id)
            putExtra(Const.SYSTEM_TITLE, title)
        })
    }
}