package model

import kotlinx.serialization.Serializable

@Serializable
data class Statistics(val id: Long, val userId: Long, val ridesSucceededNumber: Int)
