package vinh.nguyen.app.utils

import kotlin.time.Duration.Companion.milliseconds

// Ms to MM:SS format
fun formatMsToMMSS(milliseconds: Long): String {
    val duration = milliseconds.milliseconds
    return duration.toComponents { _, minutes, seconds, _ ->
        // Format with leading zeros
        "%02d:%02d".format(minutes, seconds)
    }
}