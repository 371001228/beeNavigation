package com.bee.navigation.delegate

/**
 * @author bee
 * @date 2020/4/15.
 */
interface IBeeSupportFragment : IBeeFragmentVisibleCallback {

    fun getSupportDelegate(): BeeSupportFragmentDelegate

    fun onSupportVisible()

    fun onSupportInvisible()

    fun onFlyLazyInitView()

    fun onFlyLazyInitView2()

    fun isFlySupportVisible(): Boolean

    fun onBackPressedSupport(): Boolean
}