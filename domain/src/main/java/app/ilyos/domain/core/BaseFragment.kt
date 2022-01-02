package app.ilyos.domain.core

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import com.google.android.material.snackbar.Snackbar

abstract class BaseFragment : Fragment {

    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            runId = savedInstanceState.getString(FIRST_RUN_ID)!!

            val appId = requireContext().applicationContext.hashCode().toString()
            val savedAppId = savedInstanceState.getString(APP_RUN_ID)

            createMode = if (appId != savedAppId) {
                CreateMode.RESTORED_AFTER_DEATH
            } else {
                CreateMode.RESTORED_AFTER_ROTATION
            }
        } else {
            runId = "${javaClass.simpleName}[${hashCode()}]"
            createMode = CreateMode.NEW
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideKeyboard()
        initialize()
        requireView().setOnClickListener { hideKeyboard() }
    }

    override fun onStart() {
        super.onStart()
        instanceStateSaved = false
    }

    override fun onResume() {
        super.onResume()
        instanceStateSaved = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(APP_RUN_ID, requireContext().applicationContext.hashCode().toString())
        outState.putString(FIRST_RUN_ID, runId)
        instanceStateSaved = true
    }

    override fun onDestroy() {
        super.onDestroy()
        hideKeyboard()
        if (isRealDestroy()) onRealDestroy()
    }

    // This is android, baby!
    private fun isRealRemoving(): Boolean =
        (isRemoving && !instanceStateSaved) || // Because isRemoving == true for fragment in backstack on screen rotation
                ((parentFragment as? BaseFragment)?.isRealRemoving() ?: false)

    // It will be valid only for 'onDestroy()' method
    private fun isRealDestroy(): Boolean = when {
        activity?.isChangingConfigurations == true -> false
        activity?.isFinishing == true -> true
        else -> isRealRemoving()
    }

    protected var isLoading: Boolean
        set(value) {
            (activity as? BaseActivity)?.isLoading = value
        }
        get() = (activity as? BaseActivity)?.isLoading ?: false


    protected fun message(
        message: String,
        isError: Boolean = false,
        duration: Int = Snackbar.LENGTH_LONG,
        anchorView: View? = null
    ) = (requireActivity() as? BaseActivity)?.message(message, isError, duration, anchorView)

    protected fun navigateTo(screen: Screen) = router.navigateTo(screen)
    protected fun replaceScreen(screen: Screen) = router.replaceScreen(screen)
    protected fun newRootScreen(screen: Screen) = router.newRootScreen(screen)
    protected fun backTo(screen: Screen?) = router.backTo(screen)
    protected fun exit() = router.exit()

    abstract fun initialize()

    open fun onBackPressed(): Boolean = false

    protected open fun onRealDestroy() {}

    protected fun hideKeyboard() {
        val manager: InputMethodManager =
            requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null)
            manager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    protected lateinit var runId: String
        private set

    protected lateinit var createMode: CreateMode
        private set

    private var instanceStateSaved: Boolean = false

    companion object {
        private const val FIRST_RUN_ID = "STATE_FIRST_RUN_ID"
        private const val APP_RUN_ID = "STATE_APP_RUN_ID"
    }
}

val BaseFragment.router: Router get() = findParentRouter()!!

fun BaseFragment.findParentRouter(): Router? {
    return (this.activity as? BaseActivity)?.router
}

enum class CreateMode {
    NEW, RESTORED_AFTER_ROTATION, RESTORED_AFTER_DEATH
}