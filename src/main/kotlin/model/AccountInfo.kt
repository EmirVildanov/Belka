package model

import kotlinx.serialization.Serializable
import model.AccountInfo.Companion.CREDENTIALS_LENGTH
import model.AccountInfo.Companion.charPool
import server.userInteractor.UserState
import org.joda.time.DateTime
import server.DateTimeSerializer
import server.userInteractor.UserState.NOT_STARTED

// As soon as user must fill such fields as name and surname, by default these fields are null
@Serializable
data class AccountInfo(
    val id: Long,                  // ChatId
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: DateTime = DateTime.now(),
    val state: UserState = NOT_STARTED,
    val rating: List<UserReview> = listOf(),
    val credentials: String = generateCredentials(),       // Needed for authorization beyond Telegram
    val statistics: Long,
    val name: String? = null,
    val username: String? = null,
    // TODO(val inARideWith: Long? = null)  <-- for understanding who is user writing review to
    val about: String? = null,
    val photo: String? = null,     // FileId
    val age: Int? = null,
    val surname: String? = null
) {
    companion object {
        const val CREDENTIALS_LENGTH = 20
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val MOCK_ACCOUNT_INFO = AccountInfo(
            id = 1,
            createdAt = DateTime.now(),
            name = "Rambo",
            surname = "Terminator",
            username = "Shwartz",
            credentials = generateCredentials(),
            state = NOT_STARTED,
            rating = listOf(),
            statistics = 1
        )
    }
}

fun generateCredentials(): String {
    return (1..CREDENTIALS_LENGTH)
        .map { kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("");
}
