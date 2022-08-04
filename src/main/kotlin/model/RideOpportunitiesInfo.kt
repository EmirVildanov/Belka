package model

import kotlinx.serialization.Serializable

@Serializable
data class RideOpportunitiesInfo(val segments: List<RideInfo>)

@Serializable
data class RideInfo(val departure: String, val from: StationInfo, val to: StationInfo)

@Serializable
data class StationInfo(val title: String)
