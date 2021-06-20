package com.bee.navigation.delegate

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**
 * @author bee
 * @date 2020/4/15.
 */
class BeeSupportFragmentDelegate(private val supportF: IBeeSupportFragment) {
    private val visibleDelegate by lazy { BeeVisibleDelegate(supportF) }
    private val fragment: Fragment
    private lateinit var activity: FragmentActivity

    init {
        if (supportF !is Fragment) {
            error("${supportF.javaClass.simpleName} must extends Fragment")
        }
        fragment = supportF
    }

    fun onAttach() {
        val activity = fragment.requireActivity()
        if (activity !is IBeeSupportActivity) {
            error("${activity.javaClass.simpleName} must impl IFlySupportActivity!")
        }
        this.activity = activity
    }

    fun onResume() {
        visibleDelegate.onResume()
    }

    fun onPause() {
        visibleDelegate.onPause()
    }

    fun onDestroyView() {
        visibleDelegate.onDestroyView()
    }

    fun isSupportVisible() = visibleDelegate.isSupportVisible()

    fun onBackPressedSupport() = false
}