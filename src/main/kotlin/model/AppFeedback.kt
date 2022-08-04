package model

import kotlinx.serialization.Serializable

@Serializable
data class AppFeedback(val rate: Int, val feedback: String)
