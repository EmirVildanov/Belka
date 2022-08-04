package server

import model.RideInfo
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat

object TimeWorker {
    private val zoneMoscow = DateTimeZone.forID("Europe/Moscow")
    private val rideTimeFormatter = ISODateTimeFormat.hourMinute();

    fun getCurrentTime(): LocalDate {
        return LocalDate.now(zoneMoscow)
    }

    fun getRideHourMinutesString(date: LocalDate): String {
        return rideTimeFormatter.print(date)
    }

    fun isRideYetAvailable(rideInfo: RideInfo): Boolean {
        return DateTime(rideInfo.departure, zoneMoscow).toLocalTime() > getCurrentTime()
    }
}