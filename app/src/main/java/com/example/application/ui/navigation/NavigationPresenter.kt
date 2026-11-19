package  com.example.application.ui.navigation

import android.content.Intent
import android.view.View
import com.example.application.bean.ArticleEntity
import com.example.application.ui.browser.BrowserNormalActivity
import com.example.common.constant.Const

/**
 * @author cy
 * Create at 2020/5/9.
 */
class NavigationPresenter {

    val clickLabel: (View, ArticleEntity.DatasBean) -> Unit =
        { view, datasBean -> clickLabel(view, datasBean) }

    private fun clickLabel(view: View, datasBean: ArticleEntity.DatasBean) {
        val context = view.context ?: return
        val intent = Intent(context, BrowserNormalActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Const.WEB_TITLE, datasBean.title)
            putExtra(Const.WEB_URL, datasBean.link)
        }
        context.startActivity(intent)
    }
}