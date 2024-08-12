package org.lemonadestand.btb.extenstions

import java.util.Calendar
import java.util.Date

fun Date.ago(): String {
    val SECOND_MILLIS = 1000
    val MINUTE_MILLIS = 60 * SECOND_MILLIS
    val HOUR_MILLIS = 60 * MINUTE_MILLIS
    val DAY_MILLIS = 24 * HOUR_MILLIS

    val curDate = Calendar.getInstance().time
    if (time < 1000000000000L) {
        time *= 1000
    }

    val now = curDate.time
    if (time > now || time <= 0) {
        return "in the future"
    }

    val diff = now - time
    return when {
        diff < MINUTE_MILLIS -> "moments ago"
        diff < 2 * MINUTE_MILLIS -> "1 minute ago"
        diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} minutes ago"
        diff < 2 * HOUR_MILLIS -> "1 hour ago"
        diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} hours ago"
        diff < 48 * HOUR_MILLIS -> "1 day ago"
        diff / DAY_MILLIS < 30 -> "${diff / DAY_MILLIS} days ago"
        diff / DAY_MILLIS < 60 -> "1 month ago"
        else -> "${diff / DAY_MILLIS / 30} months ago"
    }
}
