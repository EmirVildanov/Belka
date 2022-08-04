package model

import kotlinx.serialization.Serializable

@Serializable
data class UserReview(val accountId: Int, val rate: Int, val feedback: String)
