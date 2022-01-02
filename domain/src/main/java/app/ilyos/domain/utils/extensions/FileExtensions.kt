package app.ilyos.domain.utils.extensions

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

private val nollBuffer = ByteArray(8) { 0 }

fun FileInputStream.encodeAndCopyTo(out: FileOutputStream): Long {
    val bufferSize = 1024
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        out.write(nollBuffer, 0, 8)
        bytesCopied += bytes
        bytes = read(buffer)
    }
    return bytesCopied
}

fun FileInputStream.decodeAndCopyTo(out: FileOutputStream): Long {
    val bufferSize = 1024
    var bytesCopied: Long = 0
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        val a = read(nollBuffer, 0, 8)
        bytesCopied += bytes
        bytes = read(buffer)
    }
    return bytesCopied
}

fun File.encodeAndCopyTo(out: File): Long {
    val input = FileInputStream(this)
    val output = FileOutputStream(out)
    return input.encodeAndCopyTo(output)
}

fun File.decodeAndCopyTo(out: File): Long {
    val input = FileInputStream(this)
    val output = FileOutputStream(out)
    return input.decodeAndCopyTo(output)
}

fun getCurrentTimeStamp(): String {
    return SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
}

