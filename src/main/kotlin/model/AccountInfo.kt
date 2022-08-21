package model

import kotlinx.serialization.Serializable
import server.userInteractor.UserState
import org.joda.time.DateTime
import server.DateTimeSerializer
import server.userInteractor.UserState.NOT_STARTED

// As soon as user must fill such fields as name and surname, by default these fields are null
@Serializable
data class AccountInfo(
    val id: Long,                  // ChatId
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: DateTime,
    val state: UserState = NOT_STARTED,
    val rating: List<UserReview> = listOf(),
    val statistics: Statistics?,
    val name: String? = null,
    val username: String? = null,
    val about: String? = null,
    val photo: String? = null,     // FileId
    val age: Int? = null,
    val surname: String? = null
) {
    companion object {
        val MOCK_ACCOUNT_INFO = AccountInfo(
            id = 1,
            createdAt = DateTime.now(),
            name = "Rambo",
            surname = "Terminator",
            username = "Shwartz",
            state = NOT_STARTED,
            rating = listOf(),
            statistics = Statistics(1, 1,0)
        )
    }
}
