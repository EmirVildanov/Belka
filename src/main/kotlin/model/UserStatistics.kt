package model

import kotlinx.serialization.Serializable

@Serializable
data class UserStatistics(val id: Int, val ridesSucceededNumber: Int)
