package model

import java.util.UUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.AccountInfo.UUIDSerializer
import utils.Utils.generateNewUUID

@Serializable
data class UserReview(
    @SerialName("_id")
    @Serializable(with = UUIDSerializer::class)
    val userReviewId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val fromAccountId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val toAccountId: UUID,
    val feedback: String
) {
    companion object {
        fun createNewUserReview(fromAccountId: UUID, toAccountId: UUID, feedback: String): UserReview {
            return UserReview(
                userReviewId = generateNewUUID(),
                fromAccountId = fromAccountId,
                toAccountId = toAccountId,
                feedback = feedback
            )
        }
    }
}
