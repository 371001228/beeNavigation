package com.bee.navigation.page

import androidx.appcompat.app.AppCompatActivity
import com.bee.navigation.delegate.BeeSupportActivityDelegate
import com.bee.navigation.delegate.IBeeSupportActivity

/**
 * @author bee
 * @date 2020/4/15.
 */
open class BeeSupportActivity : AppCompatActivity(), IBeeSupportActivity {
    private val delegate by lazy { BeeSupportActivityDelegate(this) }

    override fun getSupportDelegate() = delegate

    /**
     * 不建议复写该方法，请使用 [onBackPressedSupport] 代替
     */
    override fun onBackPressed() {
        delegate.onBackPressed()
    }

    /**
     * 该方法回调时机为，Activity 回退栈内 Fragment 的数量小于等于1时，默认 finish Activity
     * 请尽量复写该方法，避免复写 [onBackPressed]，以保证 [BeeSupportFragment]
     * 内的 [BeeSupportFragment.onBackPressedSupport] 回退事件正常执行
     */
    override fun onBackPressedSupport() {
        delegate.onBackPressedSupport()
    }
}