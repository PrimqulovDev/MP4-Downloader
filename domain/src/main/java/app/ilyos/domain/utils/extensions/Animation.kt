package app.ilyos.domain.utils.extensions

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.fragment.app.Fragment

fun Animation.setAnimationEndListener(listener: () -> Unit) {
    setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
            listener()
        }

        override fun onAnimationStart(animation: Animation?) {

        }

    })
}

fun Animation.setAnimationStartListener(listener: () -> Unit) {
    setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
        }

        override fun onAnimationEnd(animation: Animation?) {
        }

        override fun onAnimationStart(animation: Animation?) {
            listener()
        }

    })
}

fun Fragment.loadAnimation(@AnimRes id: Int): Animation =
    AnimationUtils.loadAnimation(context, id)
