package  com.wan.android.ui.setting

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.color.ColorPalette
import com.afollestad.materialdialogs.color.colorChooser
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.wan.android.R
import com.wan.android.app.GlobalViewModel
import com.wan.android.databinding.ActivitySettingBinding
import com.wan.core.ext.appViewModels
import com.wan.core.ext.collectOnLifecycle
import com.wan.core.mvvm.BaseVMActivity
import com.wan.core.utils.SettingUtil
import com.wan.core.utils.StringUtils
import com.wan.core.utils.versionName
import dagger.hilt.android.AndroidEntryPoint
import splitties.views.onClick


/**
 * @author cy
 * Create at 2020/4/10.
 */
@AndroidEntryPoint
class SettingActivity : BaseVMActivity<SettingViewModel, ActivitySettingBinding>() {

    private val settingViewModel by viewModels<SettingViewModel>()
    private val globalViewModel by appViewModels<GlobalViewModel>()

    override fun getLayoutId(): Int = R.layout.activity_setting

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initView() {
        setTitle(R.string.setting)
        binding.tvClearValue.onClick { settingViewModel.clearCache() }
        binding.tvVersionValue.text = StringUtils.format("v %s", versionName)
        binding.tvColor.onClick { setThemeColor() }
    }

    @SuppressLint("CheckResult")
    private fun setThemeColor() {
        MaterialDialog(this).show {
            title(R.string.choose_theme_color)
            colorChooser(
                ColorPalette.Primary,
                ColorPalette.PrimarySub
            ) { _, color ->
                SettingUtil.setColor(color)
                initColor()
                globalViewModel.changeThemeColor(color)
            }
            positiveButton(R.string.done)
            negativeButton(android.R.string.cancel)
            lifecycleOwner(this@SettingActivity)
        }
    }

    override fun loadData() {
        settingViewModel.getCacheSize()
    }

    override fun subscribeUi() {
        settingViewModel.cacheValue.collectOnLifecycle(this) { cacheValue ->
            binding.tvClearValue.text = cacheValue
        }
    }
}