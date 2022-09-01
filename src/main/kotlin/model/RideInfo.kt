package model

import kotlinx.serialization.Serializable
import model.enum.TransportType
import org.joda.time.DateTime
import utils.DateTimeSerializer

@Serializable
data class RideInfo(
    val fromStationCode: String,
    val toStationCode: String,
    @Serializable(with = DateTimeSerializer::class)
    val departureAt: DateTime,
    val transportType: TransportType,
)