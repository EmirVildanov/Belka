package model

import kotlinx.serialization.Serializable
import org.joda.time.DateTime
import server.DateTimeSerializer

@kotlinx.serialization.Serializable
data class Application(
    val id: Int,
    val from: Int,
    val pointFrom: Int,
    val pointTo: Int,
    @Serializable(with = DateTimeSerializer::class)
    val time: DateTime,
    val comment: String
)
