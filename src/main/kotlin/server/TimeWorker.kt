package server

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import model.RideInfo
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat

object TimeWorker {
    private val zoneMoscow = DateTimeZone.forID("Europe/Moscow")
    private val rideTimeFormatter = ISODateTimeFormat.hourMinute()

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

object DateTimeSerializer : KSerializer<DateTime> {
    override val descriptor = PrimitiveSerialDescriptor("DateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): DateTime {
        return DateTime(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: DateTime) {
        encoder.encodeString(value.toString())
    }
}
