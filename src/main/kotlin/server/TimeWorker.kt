package server

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

object TimeWorker {
    val ZONE_MOSCOW: DateTimeZone = DateTimeZone.forID("Europe/Moscow")
    val ZONE_YEKATERINBURG: DateTimeZone = DateTimeZone.forID("Asia/Yekaterinburg")
    private val rideTimeFormatter: DateTimeFormatter = ISODateTimeFormat.hourMinute()

    fun now(timeZone: DateTimeZone): DateTime {
        return DateTime.now(timeZone)
    }

    fun dateTimeFromString(dateTimeString: String, zone: DateTimeZone? = null): DateTime {
        return DateTime(dateTimeString, zone)
    }

    fun getRideHourMinutesString(dateTime: DateTime): String {
        return rideTimeFormatter.print(dateTime)
    }

    fun isDateTimeAvailable(dateTime: DateTime): Boolean {
        val zone = ZONE_MOSCOW
        return DateTime(dateTime, zone) > now(zone)
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
}
