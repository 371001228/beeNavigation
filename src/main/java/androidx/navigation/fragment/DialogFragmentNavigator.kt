package androidx.navigation.fragment

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.*
import androidx.navigation.NavDestination.ClassType
import androidx.navigation.fragment.DialogFragmentNavigator.Destination
import com.bee.navigation.R

/**
 * Navigator that uses [DialogFragment.show]. Every
 * destination using this Navigator must set a valid DialogFragment class name with
 * `android:name` or [Destination.setClassName].
 */
@Navigator.Name("dialog")
class DialogFragmentNavigator internal constructor(
    private val context: Context,
    private val fragmentManager: FragmentManager
) : Navigator<Destination>() {
    private var dialogCount = 0
    private val observer by lazy {
        LifecycleEventObserver { source: LifecycleOwner, event: Lifecycle.Event ->
            if (event == Lifecycle.Event.ON_STOP) {
                val dialogFragment = source as? DialogFragment ?: return@LifecycleEventObserver
                if (!dialogFragment.requireDialog().isShowing) {
                    try {
                        NavHostFragment.findNavController(dialogFragment).popBackStack()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun popBackStack(): Boolean {
        if (dialogCount == 0) {
            return false
        }
        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state")
            return false
        }
        fragmentManager.findFragmentByTag(DIALOG_TAG + --dialogCount)?.apply {
            lifecycle.removeObserver(observer)
            try {
                (this as? DialogFragment)?.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return true
    }

    override fun createDestination() = Destination(this)

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        if (fragmentManager.isStateSaved) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state")
            return null
        }
        try {
            var className = destination.className
            if (className[0] == '.') {
                className = context.packageName + className
            }
            val frag = fragmentManager.fragmentFactory.instantiate(context.classLoader, className)
            require(DialogFragment::class.java.isAssignableFrom(frag.javaClass)) {
                "Dialog destination ${destination.className} is not an instance of DialogFragment"
            }
            if (frag is DialogFragment && frag.isAdded) {
                frag.arguments = args
                frag.lifecycle.addObserver(observer)
                frag.show(fragmentManager, DIALOG_TAG + dialogCount++)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return destination
    }

    override fun onSaveState(): Bundle? {
        if (dialogCount == 0) {
            return null
        }
        return Bundle().apply {
            putInt(KEY_DIALOG_COUNT, dialogCount)
        }
    }

    override fun onRestoreState(savedState: Bundle) {
        dialogCount = savedState.getInt(KEY_DIALOG_COUNT, 0)
        for (index in 0 until dialogCount) {
            val fragment = fragmentManager.findFragmentByTag(DIALOG_TAG + index) as? DialogFragment
            if (fragment != null) {
                fragment.lifecycle.addObserver(observer)
            } else {
                error("DialogFragment $index doesn't exist in the FragmentManager")
            }
        }
    }

    /**
     * NavDestination specific to [DialogFragmentNavigator].
     */
    @ClassType(DialogFragment::class)
    class Destination
    /**
     * Construct a new fragment destination. This destination is not valid until you set the
     * Fragment via [.setClassName].
     *
     * @param fragmentNavigator The [DialogFragmentNavigator] which this destination
     * will be associated with. Generally retrieved via a
     * [NavController]'s
     * [NavigatorProvider.getNavigator] method.
     */
    internal constructor(fragmentNavigator: Navigator<out Destination>) :
        NavDestination(fragmentNavigator), FloatingWindow {
        private var mClassName: String? = null

        /**
         * Construct a new fragment destination. This destination is not valid until you set the
         * Fragment via [.setClassName].
         *
         * @param navigatorProvider The [NavController] which this destination
         * will be associated with.
         */
        internal constructor(navigatorProvider: NavigatorProvider) : this(
            navigatorProvider.getNavigator<DialogFragmentNavigator>(DialogFragmentNavigator::class.java)
        )

        @CallSuper
        override fun onInflate(
            context: Context,
            attrs: AttributeSet
        ) {
            super.onInflate(context, attrs)
            val ta = context.resources.obtainAttributes(attrs, R.styleable.DialogFragmentNavigator)
            ta.getString(R.styleable.DialogFragmentNavigator_android_name)?.let { setClassName(it) }
            ta.recycle()
        }

        /**
         * Set the DialogFragment class name associated with this destination
         *
         * @param className The class name of the DialogFragment to show when you navigate to this
         * destination
         * @return this [Destination]
         */
        fun setClassName(className: String): Destination {
            mClassName = className
            return this
        }

        /**
         * Gets the DialogFragment's class name associated with this destination
         *
         * @throws IllegalStateException when no DialogFragment class was set.
         */
        val className: String
            get() {
                return checkNotNull(mClassName) { "DialogFragment class was not set" }
            }
    }

    companion object {
        private const val TAG = "DialogFragmentNavigator"
        private const val KEY_DIALOG_COUNT = "androidx-nav-dialogfragment:navigator:count"
        private const val DIALOG_TAG = "androidx-nav-fragment:navigator:dialog:"
    }
}