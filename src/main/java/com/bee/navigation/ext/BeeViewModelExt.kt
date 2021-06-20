package com.bee.navigation.ext

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.bee.navigation.BeeApplication

/**
 * 获取 Application 级别的 ViewModel
 */
inline fun <reified VM : ViewModel> LifecycleOwner.lazyAppViewModel(): Lazy<VM> {
    return lazy {
        when (this) {
            is Fragment -> {
                (this.requireActivity().applicationContext as BeeApplication)
                    .getAppViewModelProvider().get(VM::class.java)
            }
            is AppCompatActivity -> {
                (this.applicationContext as BeeApplication)
                    .getAppViewModelProvider().get(VM::class.java)
            }
            else -> {
                error("${this.javaClass.simpleName} must impl Fragment or AppCompatActivity!")
            }
        }
    }
}