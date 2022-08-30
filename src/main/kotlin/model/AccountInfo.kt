package model

import java.util.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import server.userInteractor.UserState
import org.joda.time.DateTime
import server.TimeWorker.DateTimeSerializer
import server.userInteractor.UserState.NOT_STARTED
import utils.Utils.generateNewUUID

// As soon as user must fill such fields as name and surname, by default these fields are null
@Serializable
data class AccountInfo(
    @SerialName("_id")
    @Serializable(with = UUIDSerializer::class)
    val accountInfoId: UUID,  // id and chatId are separated in case application will live beyond telegram
    val chatId: Long?,  // telegram chatId
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: DateTime,
    @Serializable(with = UUIDSerializer::class)
    val statisticsId: UUID,
    val ratingIds: List<@Serializable(with = UUIDSerializer::class) UUID>,
    val applicationIds: List<@Serializable(with = UUIDSerializer::class) UUID>,
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
        fun createNewAccount(chatId: Long?, statisticsId: UUID): AccountInfo {
            return AccountInfo(
                accountInfoId = generateNewUUID(),
                chatId = chatId,
                createdAt = DateTime.now(),
                statisticsId = statisticsId,
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

    object UUIDSerializer : KSerializer<UUID> {
        override val descriptor = PrimitiveSerialDescriptor("DateTime", PrimitiveKind.STRING)

        override fun deserialize(decoder: Decoder): UUID {
            return UUID.fromString(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: UUID) {
            encoder.encodeString(value.toString())
        }
    }
}
