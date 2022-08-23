package model

import kotlinx.serialization.Serializable

@Serializable
data class AppFeedback(val id: Int, val from: Int, val rate: Int, val feedback: String?)
