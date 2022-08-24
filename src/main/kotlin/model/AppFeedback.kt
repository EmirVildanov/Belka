package model

import kotlinx.serialization.Serializable

@Serializable
data class AppFeedback(
    val id: Long,
    val from: Long,
    val feedback: String?
)
