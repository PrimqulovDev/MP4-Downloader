package app.ilyos.domain.core

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import app.ilyos.domain.R
import app.ilyos.domain.utils.extensions.gone
import app.ilyos.domain.utils.extensions.visible
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity : AppCompatActivity(R.layout.activity_root) {

    private val cicerone: Cicerone<Router> by lazy {
        ciceroneMap.getOrPut(ciceroneKey) { Cicerone.create() }
    }

    val router: Router
        get() = cicerone.router

    val currentFragment: BaseFragment?
        get() = supportFragmentManager.findFragmentById(navigationContainerId) as? BaseFragment

    private val navigator by lazy { createNavigator() }
    protected open fun createNavigator(): Navigator = AppNavigator(this, navigationContainerId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ciceroneKey = savedInstanceState?.getString(STATE_CICERONE_KEY)
            ?: "AppActivity_${hashCode()}"

        if (supportFragmentManager.fragments.isEmpty()) {
            router.newRootScreen(rootScreen)
        }
        topAnchorView = findViewById(R.id.coordinatorLayoutTop)
        pbMain = findViewById(R.id.pbMain)
        onActivityCreated(savedInstanceState)
    }

    fun message(
        message: String,
        isError: Boolean = false,
        duration: Int = Snackbar.LENGTH_LONG,
        anchorView: View? = null
    ) {
        if (message.isNotEmpty()) {

            if (snackBar == null) {
                snackBar = Snackbar.make(
                    anchorView ?: topAnchorView ?: findViewById(android.R.id.content),
                    "",
                    duration
                )

                val layout = snackBar?.view as  Snackbar.SnackbarLayout
                val textView = layout.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

                textView.textSize = 19f
                textView.gravity = Gravity.CENTER
                snackBar!!.setActionTextColor(Color.WHITE)
            }

            snackBar!!.setText(message)

            val sbView = snackBar!!.view

            sbView.setBackgroundResource(
                if (isError) R.drawable.bg_snack_bar_error else R.drawable.bg_snack_bar
            )
            snackBar!!.show()
        }
    }

    private var topAnchorView: CoordinatorLayout? = null
    private var snackBar: Snackbar? = null

    var isLoading: Boolean
        get() = pbMain?.isVisible ?: false
        set(value) {
            if (value) pbMain?.visible() else pbMain?.gone()
        }

    open fun onActivityCreated(savedInstanceState: Bundle?) {}

    override fun onResumeFragments() {
        super.onResumeFragments()
        cicerone.getNavigatorHolder().setNavigator(navigator)
    }

    override fun onPause() {
        cicerone.getNavigatorHolder().removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            ciceroneMap.remove(ciceroneKey)
        }
    }

    override fun onBackPressed() {
        if (currentFragment?.onBackPressed() != true) {
            super.onBackPressed()
        }
    }

    companion object {
        private const val STATE_CICERONE_KEY = "STATE_CICERONE_KEY"
        private val ciceroneMap = mutableMapOf<String, Cicerone<Router>>()
    }

    private lateinit var ciceroneKey: String

    private val navigationContainerId: Int = R.id.mainContainer
    protected abstract val rootScreen: Screen
    private var pbMain: FrameLayout? = null

}