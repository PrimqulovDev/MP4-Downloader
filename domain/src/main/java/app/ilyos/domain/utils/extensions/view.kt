package app.ilyos.domain.utils.extensions

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat

fun View.gone(): View {
    visibility = View.GONE
    return this
}

fun View.invisible(): View {
    visibility = View.INVISIBLE
    return this
}

fun View.visible(): View {
    visibility = View.VISIBLE
    return this
}

fun View.enable(): View {
    isEnabled = true
    alpha = 1f
    isClickable = true
    return this
}

fun View.disable(): View {
    isEnabled = false
    alpha = 0.3f
    isClickable = false
    return this
}

fun View.inDevelopment() {
    setOnClickListener {
        Toast.makeText(context, "In Development", Toast.LENGTH_SHORT).show()
    }
}


fun loge(message: String, tag: String = "RRR") {
    Log.e(tag, message)
}

fun logw(message: String, tag: String = "RRR") {
    Log.w(tag, message)
}


internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(ContextCompat.getColor(context, color))

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)




