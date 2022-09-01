package model

import db.MongoDbConnector
import java.util.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.joda.time.DateTime
import server.TimeWorker
import server.userInteractor.UserState
import server.userInteractor.UserState.NOT_STARTED
import utils.DateTimeSerializer
import utils.Utils.generateNewUUID

// As soon as user must fill such fields as name and surname, by default these fields are null
@Serializable
data class AccountInfo(
    @SerialName("_id")
    @Contextual
    val accountInfoId: UUID,  // id and chatId are separated in case application will live beyond telegram
    val chatId: Long?,  // telegram chatId
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: DateTime,
    @Contextual
    val statisticsId: UUID,
    val ratingIds: List<@Contextual UUID>,
    val applicationIds: List<@Contextual UUID>,
    val credentials: String, // Needed for authorization beyond Telegram
    val state: UserState,
    val name: String?,
    val surname: String?,
    val age: Int?,
    val username: String?,
    val about: String?,
    val photoFileId: String?  // telegram FileId
) {
    companion object {
        private const val CREDENTIALS_LENGTH = 20
        private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        suspend fun createNewAccount(chatId: Long?): AccountInfo {
            val accountId = generateNewUUID()
            return AccountInfo(
                accountInfoId = accountId,
                chatId = chatId,
                createdAt = TimeWorker.now(TimeWorker.ZONE_MOSCOW),
                statisticsId = MongoDbConnector.addNewStatistics(accountId).statisticsId,
                ratingIds = listOf(),
                applicationIds = listOf(),
                credentials = generateNewCredentials(),
                state = NOT_STARTED,
                name = null,
                surname = null,
                age = null,
                username = null,
                about = null,
                photoFileId = null
            )
        }

        private fun generateNewCredentials(): String {
            return (1..CREDENTIALS_LENGTH)
                .map { kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");
        }
    }
}
