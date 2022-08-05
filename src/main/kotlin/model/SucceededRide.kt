package model

import kotlinx.serialization.Serializable
import org.joda.time.DateTime
import server.DateTimeSerializer

@Serializable
data class SucceededRide(
    val id: Int,
    @Serializable(with = DateTimeSerializer::class)
    val dateTime: DateTime,
    val from: StationInfo,
    val to: StationInfo,
    val userIds: List<Int>
)
