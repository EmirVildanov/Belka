package server.rides

import kotlinx.serialization.Serializable

// These are internal data classes for deserializing Yandex Rasp responses.

/**
 * Ride opportunities for
 * https://yandex.ru/dev/rasp/doc/reference/schedule-point-point.html
 * request.
 */
@Serializable
data class RideOpportunitiesInfo(val segments: List<RideInfo>)

/**
 * Concrete rideOpportunity from the big list.
 */
@Serializable
data class RideInfo(val departure: String, val from: StationInfo, val to: StationInfo)

/**
 * Concrete station info.
 */
@Serializable
data class StationInfo(val title: String)
