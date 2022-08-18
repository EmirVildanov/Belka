package model

import kotlinx.serialization.Serializable
import model.enum.UserState
import org.joda.time.DateTime
import server.DateTimeSerializer

@Serializable
data class AccountInfo(
    // ChatId
    val id: Int,
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: DateTime,
    val name: String,
    val surname: String?,
    val username: String,
    val age: Int?,
    val about: String,
    val state: UserState,
    val rating: List<UserReview>,
    val statistics: UserStatistics,
    // FileId
    val photo: String
)
