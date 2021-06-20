package com.bee.navigation.delegate

/**
 * @author bee
 * @date 2020/4/15.
 */
interface IBeeSupportActivity {

    fun getSupportDelegate(): BeeSupportActivityDelegate

    fun onBackPressed()

    fun onBackPressedSupport()
}