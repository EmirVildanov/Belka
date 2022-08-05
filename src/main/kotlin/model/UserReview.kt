package model

import kotlinx.serialization.Serializable

@Serializable
data class UserReview(val Id: Int, val from: Int, val to: Int, val rate: Int, val feedback: String)

fun calculateUserNumberRating(reviews: List<UserReview>): Int {
    return reviews.sumOf { it.rate } / reviews.size
}
