package com.bee.navigation.delegate

import androidx.fragment.app.Fragment

/**
 * @author bee
 * @date 2020/5/12.
 */
class BeeVisibleDelegate(private val supportF: IBeeSupportFragment) {
    private var isSupportVisible = false
    private var fragment: Fragment

    init {
        if (supportF !is Fragment) {
            error("must extends Fragment")
        }
        fragment = supportF
    }

    fun onResume() {
        if (!fragment.isHidden && !isSupportVisible) {
            isSupportVisible = true
            supportF.onSupportVisible()
        }
    }

    fun onPause() {
        if (isSupportVisible) {
            isSupportVisible = false
            supportF.onSupportInvisible()
        }
    }

    fun onDestroyView() {
        isSupportVisible = false
    }

    fun isSupportVisible() = isSupportVisible
}