package model

import java.util.UUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import model.AccountInfo.UUIDSerializer
import utils.Utils.generateNewUUID

@Serializable
data class AppFeedback(
    @SerialName("_id")
    @Serializable(with = UUIDSerializer::class)
    val appFeedbackId: UUID,
    @Serializable(with = UUIDSerializer::class)
    val fromAccountId: UUID,
    val feedback: String
) {
    companion object {
        fun createNewAppFeedBack(fromAccountId: UUID, feedback: String): AppFeedback {
            return AppFeedback(
                appFeedbackId = generateNewUUID(),
                fromAccountId = fromAccountId,
                feedback = feedback
            )
        }
    }
}
