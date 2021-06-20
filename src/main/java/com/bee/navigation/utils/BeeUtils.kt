package com.bee.navigation.utils

import android.app.Application

/**
 * @author bee
 * @date 2021/5/14.
 */
object BeeUtils {
    private var application: Application? = null

    fun init(app: Application?) {
        if (app == null) return
        if (application == null) {
            application = app
            return
        }
        if (application?.equals(app) == true) return
        application = app
    }

    fun getApp(): Application {
        return checkNotNull(application) { "application is null，you must first call init" }
    }
}