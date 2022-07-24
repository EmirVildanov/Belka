import kotlinx.serialization.Serializable

@Serializable
data class AccountInfo(
    val id: Int,
    val name: String,
    val surname: String,
    val age: Int,
    val state: UserState
) {
}