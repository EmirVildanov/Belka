package model

import java.util.UUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.AccountInfo.UUIDSerializer
import org.joda.time.DateTime
import server.userInteractor.UserState.NOT_STARTED
import utils.Utils.generateNewUUID

@Serializable
data class Statistics(
    @SerialName("_id")
    @Serializable(with = UUIDSerializer::class)
    val statisticsId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val accountId: UUID,
    val ridesSucceededNumber: Int
) {
    companion object {
        fun createNewStatistics(accountId: UUID): Statistics {
            return Statistics(
                statisticsId = generateNewUUID(),
                accountId = accountId,
                ridesSucceededNumber = 0
            )
        }
    }
}
