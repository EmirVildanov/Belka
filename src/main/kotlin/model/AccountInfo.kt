package model

import kotlinx.serialization.Serializable
import model.enum.UserState
import org.joda.time.DateTime
import server.DateTimeSerializer

@Serializable
data class AccountInfo(
    // ChatId
    val id: Long,
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: DateTime,
    val name: String,
    val username: String,
    val about: String,
    val state: UserState,
    val rating: List<UserReview>,
    val statistics: UserStatistics,
    val newField: Int,
    // FileId
    val photo: String? = null,
    val age: Int? = null,
    val surname: String? = null
) {
    companion object {
        val MOCK_ACCOUNT_INFO = AccountInfo(
            1,
            DateTime.now(),
            "Rambo",
            "Terminator",
            "Shwartz",
            UserState.NOT_STARTED,
            listOf(),
            UserStatistics(1, 0),
            1
        )
    }
}
