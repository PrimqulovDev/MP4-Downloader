package app.ilyos.domain.utils.extensions

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.widget.EditText


val EditText.stringText: String
    get() {
        return text.toString()
    }

fun EditText.isNotEmpty() = stringText.isNotEmpty()
fun EditText.isEmpty() = stringText.isEmpty()

fun EditText.addAfterTextChangedListener(listener: (s: Editable?) -> Unit) {

    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            listener(s)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })

}

@SuppressLint("ClickableViewAccessibility")
fun EditText.onnTouchListener(
    onDown: () -> Unit = {
        requestFocus()
        error = null
    },
    onUp: () -> Unit = {}

) {
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onDown()
            MotionEvent.ACTION_UP -> onUp()
        }
        true
    }
}

fun String.toNumber(): Float? {
    var num = 0F
    this.forEach {
        if (it.isDigit()) {
            num = num * 10 + (it - '0')
        } else return null
    }
    return num
}

fun String.cleanPhone(): String {
    val p = this
    var phone = "998"
    p.forEach {
        if (it != '-' && it != ' ') {
            phone += it
        }
    }
    return phone
}