package server.rides

import kotlinx.serialization.Serializable

// These are internal data classes for deserializing Yandex Rasp responses.

/**
 * Ride opportunities for
 * https://yandex.ru/dev/rasp/doc/reference/schedule-point-point.html
 * request.
 */
@Serializable
data class RideInfo(val segments: List<RideInfoSegment>) {

    /** Concrete rideOpportunity from the big list. */
    @Serializable
    data class RideInfoSegment(
        val arrival: String,
        val from: RideInfoSegmentStationInfo,
        val thread: RideInfoSegmentThread,
        val departure_platform: String,
        val departure: String,
        val stops: String,
        val departure_terminal: String,
        val to: RideInfoSegmentStationInfo,
        val arrival_terminal: String,
        val start_date: String,
    ) {

        @Serializable
        data class RideInfoSegmentStationInfo(
            val code: String,
            val title: String,
            val station_type: String,
            val station_type_name: String,
            val popular_title: String,
            val short_title: String,
            val transport_type: String,
            val type: String,
        )

        @Serializable
        data class RideInfoSegmentThread(
            val uid: String,
            val title: String
        )
    }
}
