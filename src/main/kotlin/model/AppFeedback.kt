package model

import java.util.UUID
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.Utils.generateNewUUID

@Serializable
data class AppFeedback(
    @SerialName("_id")
    @Contextual
    val appFeedbackId: UUID,
    @Contextual
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
