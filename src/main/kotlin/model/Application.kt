package model

import kotlinx.serialization.Serializable
import org.joda.time.DateTime
import server.DateTimeSerializer
import server.rides.RideInfo

@kotlinx.serialization.Serializable
data class Application(
    val id: Long,
    val from: Int,             // UserId
    val rideInfo: RideInfo,
    @Serializable(with = DateTimeSerializer::class)
    val dateTime: DateTime,
    val comment: String?
)
