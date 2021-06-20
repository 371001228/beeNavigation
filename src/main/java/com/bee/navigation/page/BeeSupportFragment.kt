package com.bee.navigation.page

import android.content.Context
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bee.navigation.delegate.BeeSupportFragmentDelegate
import com.bee.navigation.delegate.IBeeFragmentVisibleCallback
import com.bee.navigation.delegate.IBeeSupportFragment

/**
 * @author bee
 * @date 2020/4/13.
 */
open class BeeSupportFragment : Fragment(), IBeeSupportFragment {
    private val delegate by lazy { BeeSupportFragmentDelegate(this) }
    protected val fragmentTag: String by lazy { this.javaClass.simpleName }
    private var fragmentVisibleCallbackList: ArrayList<IBeeFragmentVisibleCallback>? = null

    protected lateinit var activity: AppCompatActivity

    //是否是第一次加载数据，防止多次加载数据
    private var isFirstLoadData = true

    //懒加载初始化优化，防止页面切换动画还未执行完毕时进行数据加载导致渲染卡顿现象
    private var hasCreateAnimation = false
    private var isAnimationEnd = false

    override fun getSupportDelegate() = delegate

    override fun onAttach(context: Context) {
        super.onAttach(context)
        delegate.onAttach()
        activity = context as AppCompatActivity
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        delegate.onPause()
    }

    override fun onDestroyView() {
        delegate.onDestroyView()
        isFirstLoadData = true
        hasCreateAnimation = false
        isAnimationEnd = false
        clearFragmentVisibleCallbacks()
        super.onDestroyView()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (nextAnim <= 0) {
            isAnimationEnd = true
            return super.onCreateAnimation(transit, enter, nextAnim)
        }

        //导航图中app:startDestination设定的fragment没有执行onCreateAnimation使用
        hasCreateAnimation = true
        return AnimationUtils.loadAnimation(context, nextAnim).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    if (enter) {
                        isAnimationEnd = true
                        tryLazyLoad()
                    }
                }
            })
        }
    }

    /**
     * 执行懒加载初始化
     */
    private fun tryLazyLoad() {
        if (isFirstLoadData
            && isFlySupportVisible()
            && (!hasCreateAnimation || isAnimationEnd)
        ) {
            isFirstLoadData = false
            onFlyLazyInitView()
            onFlyLazyInitView2()
        }
    }

    /**
     * Fragment 对用户可见时
     */
    final override fun onSupportVisible() {
        onFlySupportVisible()
        tryLazyLoad()
    }

    /**
     * Fragment 对用户不可见时
     */
    override fun onSupportInvisible() {
        onFlySupportInvisible()
    }

    /**
     * 用于某些场景的懒加载，比如 FragmentAdapter 的懒加载、同级 Fragment 切换的懒加载
     */
    @CallSuper
    override fun onFlyLazyInitView() {
    }

    /**
     * 用于某些场景的懒加载，比如 FragmentAdapter 的懒加载、同级 Fragment 切换的懒加载，备用
     */
    @CallSuper
    override fun onFlyLazyInitView2() {
    }

    /**
     * Fragment 对用户可见时
     */
    @CallSuper
    override fun onFlySupportVisible() {
        fragmentVisibleCallbackList?.forEach { it.onFlySupportVisible() }
    }

    /**
     * Fragment 对用户不可见时
     */
    @CallSuper
    override fun onFlySupportInvisible() {
        fragmentVisibleCallbackList?.forEach { it.onFlySupportInvisible() }
    }

    /**
     * 当 Fragment 对用户可见，执行 [onSupportVisible]
     */
    override fun isFlySupportVisible() = delegate.isSupportVisible()

    /**
     * 按返回键触发,前提是 [BeeSupportActivity] 的 [BeeSupportActivity.onBackPressed] 方法能被调用
     *
     * @return false 则继续向上传递，true 则消费掉该事件
     */
    override fun onBackPressedSupport() = delegate.onBackPressedSupport()

    /**
     * 添加 Fragment 真实可见性回调
     */
    fun addFragmentVisibleCallbacks(fragmentVisibleCallback: IBeeFragmentVisibleCallback) {
        if (fragmentVisibleCallbackList == null) {
            fragmentVisibleCallbackList = arrayListOf()
        }
        if (fragmentVisibleCallbackList?.contains(fragmentVisibleCallback) != true) {
            fragmentVisibleCallbackList?.add(fragmentVisibleCallback)
        }
    }

    /**
     * 移除 Fragment 真实可见性回调
     */
    fun removeFragmentVisibleCallbacks(fragmentVisibleCallback: IBeeFragmentVisibleCallback) {
        if (fragmentVisibleCallbackList?.contains(fragmentVisibleCallback) == true) {
            fragmentVisibleCallbackList?.remove(fragmentVisibleCallback)
        }
    }

    /**
     * 清除 Fragment 真实可见性回调
     */
    fun clearFragmentVisibleCallbacks() {
        if (fragmentVisibleCallbackList?.isNotEmpty() == true) {
            fragmentVisibleCallbackList?.clear()
        }
    }
}