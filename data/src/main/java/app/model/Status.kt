package app.model

import java.io.File


sealed class Status
data class Downloading(val percentage: Int) : Status()
data class Canceled(val message: String) : Status()
data class Success(val file: File) : Status()
object Paused : Status()