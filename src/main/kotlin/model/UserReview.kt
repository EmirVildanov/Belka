package model

import kotlinx.serialization.Serializable

@Serializable
data class UserReview(
    val id: Long,
    val from: Int,          // UserId sending review
    val to: Int,            // UserId receiving review
    val feedback: String
)
