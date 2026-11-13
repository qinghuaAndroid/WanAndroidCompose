package  com.example.myapplication.ui.navigation

import android.content.Intent
import com.example.common.constant.Const
import com.example.core.app.App
import  com.example.myapplication.bean.ArticleEntity
import com.example.myapplication.ui.browser.BrowserNormalActivity

/**
 * @author cy
 * Create at 2020/5/9.
 */
class NavigationPresenter {

    val clickLabel: (ArticleEntity.DatasBean) -> Unit = { clickLabel(it) }

    private fun clickLabel(datasBean: ArticleEntity.DatasBean) {
        App.context().startActivity(Intent(App.context(), BrowserNormalActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Const.WEB_TITLE, datasBean.title)
            putExtra(Const.WEB_URL, datasBean.link)
        })
    }
}