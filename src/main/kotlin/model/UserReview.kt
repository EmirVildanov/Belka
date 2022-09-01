package model

import java.util.UUID
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.Utils.generateNewUUID

@Serializable
data class UserReview(
    @SerialName("_id")
    @Contextual
    val userReviewId: UUID,
    @Contextual
    val fromAccountId: UUID,
    @Contextual
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
