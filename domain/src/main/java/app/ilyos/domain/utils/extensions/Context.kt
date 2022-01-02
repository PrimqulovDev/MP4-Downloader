package app.ilyos.domain.utils.extensions

import android.content.ClipboardManager
import android.content.Context
import android.os.Environment
import java.io.File

val Context.externalOfflineDir: String
    get() {
        val dir = "${externalCacheDir?.absolutePath?.replace("cache", "files")}/offline"
        val f = getExternalFilesDir("Offline") ?: File(dir)
        if (!f.exists()) {
            f.mkdirs()
        }
        return dir
    }

fun Context.getTextFromClipboard(): String? {
    val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return if (clipboardManager.hasPrimaryClip())
        clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
    else null
}