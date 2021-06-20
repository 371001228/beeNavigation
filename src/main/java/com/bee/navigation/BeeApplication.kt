package com.bee.navigation

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.bee.navigation.utils.BeeUtils

/**
 * @author bee
 * @date 2021/5/9
 */
open class BeeApplication : Application(), ViewModelStoreOwner {

    //可借助 `Application` 来管理一个应用级的 `ViewModel`，
    //实现全应用范围内的 `生命周期安全` 且 `事件源可追溯` 的 `视图控制器` 事件通知。
    private val appViewModelStore by lazy { ViewModelStore() }
    private val factory by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)
    }

    override fun getViewModelStore() = appViewModelStore

    fun getAppViewModelProvider() = ViewModelProvider(this, factory)

    override fun onCreate() {
        super.onCreate()
        BeeUtils.init(this)
    }
}