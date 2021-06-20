package com.bee.navigation.delegate

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.bee.navigation.helper.BeeSupportHelper

/**
 * @author bee
 * @date 2020/4/15.
 */
class BeeSupportActivityDelegate(private val supportA: IBeeSupportActivity) {
    private val activity: FragmentActivity

    init {
        if (supportA !is FragmentActivity) {
            error("${supportA.javaClass.simpleName} must extends FragmentActivity")
        }
        this.activity = supportA
    }

    fun onBackPressed() {
        // 获取 activeFragment 即从栈顶开始 状态为 show 的那个 Fragment
        val activeFragment = BeeSupportHelper.getVisibleFragment(getSupportFragmentManager())
        if (!dispatchBackPressedEvent(activeFragment)) {
            supportA.onBackPressedSupport()
        }
    }

    fun onBackPressedSupport() {
        activity.onBackPressedDispatcher.onBackPressed()
    }

    private fun dispatchBackPressedEvent(activeFragment: IBeeSupportFragment?): Boolean {
        if (activeFragment != null) {
            if (activeFragment.onBackPressedSupport()) {
                return true
            }
            val parentFragment = (activeFragment as? Fragment)?.parentFragment
            return dispatchBackPressedEvent(parentFragment as? IBeeSupportFragment)
        }
        return false
    }

    private fun getSupportFragmentManager(): FragmentManager {
        return activity.supportFragmentManager
    }
}