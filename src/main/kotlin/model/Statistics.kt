package model

import java.util.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.Utils.generateNewUUID

@Serializable
data class Statistics(
    @SerialName("_id")
    @Contextual
    val statisticsId: UUID,
    @Contextual
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
