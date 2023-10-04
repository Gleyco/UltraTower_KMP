package domain

object Utils {

    fun formatMillisecondsToMin(milliseconds: Int): Int {
        return milliseconds / 60000
    }

    fun formatMinToMilliseconds(minutes: Int): Int {
        return minutes * 60000
    }

}