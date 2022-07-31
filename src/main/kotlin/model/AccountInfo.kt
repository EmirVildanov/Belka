package model

import kotlinx.serialization.Serializable

@Serializable
data class AccountInfo(
    val id: Long,
    val name: String,
    val surname: String,
    val age: Int,
    val state: UserState
) {
}