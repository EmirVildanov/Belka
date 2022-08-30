package model

import java.util.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.AccountInfo.UUIDSerializer
import model.enum.TransportType
import org.joda.time.DateTime
import server.TimeWorker.DateTimeSerializer
import server.rides.RideInfo

@kotlinx.serialization.Serializable
data class Application(
    @SerialName("_id")
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    @Serializable(with = UUIDSerializer::class)
    val createdByAccountId: UUID,
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: DateTime,
    val comment: String?,

    val fromStationCode: String,
    val toStationCode: String,
    @Serializable(with = DateTimeSerializer::class)
    val departureAt: DateTime,
    val transportType: TransportType,
    val status: ApplicationStatus,

    // exists only in case status is ACCEPTED
    val applicationAcceptionInfos: List<ApplicationAcceptionInfo>
) {
    @Serializable
    data class ApplicationAcceptionInfo(
        @Serializable(with = UUIDSerializer::class)
        val acceptedByAccountId: UUID,
        @Serializable(with = DateTimeSerializer::class)
        val acceptedAt: DateTime,
        val comment: String?
    )

    enum class ApplicationStatus {
        CREATED,
        ACCEPTED,
        MATCHED,
        DELETED
    }
}
