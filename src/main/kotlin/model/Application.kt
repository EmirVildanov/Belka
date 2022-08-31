package model

import java.util.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.AccountInfo.UUIDSerializer
import model.Application.ApplicationStatus.CREATED
import model.enum.TransportType
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import server.TimeWorker
import server.TimeWorker.DateTimeSerializer
import utils.Utils.generateNewUUID

@kotlinx.serialization.Serializable
data class Application(
    @SerialName("_id")
    @Serializable(with = UUIDSerializer::class)
    val applicationId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val createdByAccountId: UUID,
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: DateTime,
    val comment: String?,

    val rideInfo: RideInfo,
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

    companion object {
        fun createNewApplication(
            createdByAccountId: UUID,
            comment: String?,
            rideInfo: RideInfo,
            timeZone: DateTimeZone
        ): Application {
            return Application(
                applicationId = generateNewUUID(),
                createdByAccountId = createdByAccountId,
                createdAt = TimeWorker.now(timeZone),
                comment = comment,
                rideInfo = rideInfo,
                status = CREATED,
                applicationAcceptionInfos = listOf()
            )
        }
    }
}
