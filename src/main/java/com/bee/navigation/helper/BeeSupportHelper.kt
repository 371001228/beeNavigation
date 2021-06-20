package com.bee.navigation.helper

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import com.bee.navigation.delegate.IBeeSupportFragment

/**
 * @author bee
 * @date 2020/4/15.
 */
object BeeSupportHelper {

    /**
     * 获取页面可见的Fragment
     */
    fun getVisibleFragment(fragmentManager: FragmentManager): IBeeSupportFragment? {
        return getVisibleFragment(fragmentManager, null)
    }

    private fun getVisibleFragment(
        fragmentManager: FragmentManager?,
        parentFragment: IBeeSupportFragment?
    ): IBeeSupportFragment? {
        val fragmentList = getAddedFragments(fragmentManager)
        if (fragmentList.isEmpty()) {
            return parentFragment
        }
        for (i in fragmentList.indices.reversed()) {
            val fragment = fragmentList[i]
            val isFragmentVisible = isNavHostFragment(fragment)
                    || (fragment as? IBeeSupportFragment)?.isFlySupportVisible() == true

            if (isFragmentVisible) {
                return getVisibleFragment(
                    getChildFragmentManager(fragment), fragment as? IBeeSupportFragment
                )
            }
        }
        return parentFragment
    }

    fun isNavHostFragment(fragment: Fragment?): Boolean {
        return fragment is NavHostFragment
    }

    fun getAddedFragments(fragmentManager: FragmentManager?): List<Fragment> {
        return fragmentManager?.fragments ?: emptyList()
    }

    fun getChildFragmentManager(fragment: Fragment?): FragmentManager? {
        return try {
            fragment?.childFragmentManager
        } catch (ignore: Exception) {
            null
        }
    }
}