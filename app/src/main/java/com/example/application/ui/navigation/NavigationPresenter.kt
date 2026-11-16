package  com.example.application.ui.navigation

import android.content.Intent
import com.example.common.constant.Const
import com.example.core.app.App
import  com.example.application.bean.ArticleEntity
import com.example.application.ui.browser.BrowserNormalActivity

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