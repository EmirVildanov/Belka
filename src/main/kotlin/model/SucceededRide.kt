package model

import kotlinx.serialization.Serializable
import org.joda.time.DateTime
import server.DateTimeSerializer
import server.rides.StationInfo

/**
 * Application matched succeeded ride info.
 */
@Serializable
data class SucceededRide(
    val id: Long,
    @Serializable(with = DateTimeSerializer::class)
    val dateTime: DateTime,
    val from: StationInfo,
    val to: StationInfo,
    val userIds: List<Long>
)
