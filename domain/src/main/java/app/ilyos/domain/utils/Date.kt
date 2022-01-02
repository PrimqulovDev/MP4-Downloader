package app.ilyos.domain.utils

fun Long.millSecondsToLabel(): String {
    var minutes = this / (1000 * 60)
    val hours = minutes / 60
    val seconds = (this / 1000) % 60
    minutes %= 60

    val hoursStr = if (hours > 9) hours.toString() else "0$hours"
    val minutesStr = if (minutes > 9) minutes.toString() else "0$minutes"
    val secondsStr = if (seconds > 9) seconds.toString() else "0$seconds"

    return if (hours != 0L) {
        "$hoursStr:$minutesStr:$secondsStr"
    } else
        "$minutesStr:$secondsStr"

}

private fun getDurationString(duration: Long): String {
    var minutes = duration / (1000 * 60)
    val hours = minutes / 60
    val seconds = (duration / 1000) % 60
    minutes %= 60

    val hoursStr = if (hours > 9) hours.toString() else "0$hours"
    val minutesStr = if (minutes > 9) minutes.toString() else "0$minutes"
    val secondsStr = if (seconds > 9) seconds.toString() else "0$seconds"

    return if (hours != 0L) {
        "$hoursStr:$minutesStr:$secondsStr"
    } else
        "$minutesStr:$secondsStr"

}