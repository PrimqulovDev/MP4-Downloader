package app.ilyos.domain.viewbinding

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <T : ViewBinding> Fragment.viewBinding(
    binder: (View) -> T
): ReadOnlyProperty<Fragment, T> = ViewBindingCreator(binder)
/*

    object : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver {

        private var value: T? = null

        override fun onDestroy(owner: LifecycleOwner) {
            value = null
        }

        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
        }

        override fun getValue(thisRef: Fragment, property: KProperty<*>): T = value
            ?: binder(requireView()).also { setValue(it) }

        private fun setValue(value: T) {
            lifecycle.removeObserver(this)
            this.value = value
            lifecycle.addObserver(this)
        }
    }
*/


class ViewBindingCreator<T: ViewBinding>(
    private val binder: (View) -> T
) : ReadOnlyProperty<Fragment, T>, DefaultLifecycleObserver  {

    private var value: T? = null

    override fun onDestroy(owner: LifecycleOwner) {
        value = null
    }

    override fun onCreate(owner: LifecycleOwner) {
        value = null
        super.onCreate(owner)
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T = value
        ?: binder(thisRef.requireView()).also { setValue(it, thisRef) }

    private fun setValue(value: T, fragment: Fragment) {
        fragment.lifecycle.removeObserver(this)
        this.value = value
        fragment.lifecycle.addObserver(this)
    }
}