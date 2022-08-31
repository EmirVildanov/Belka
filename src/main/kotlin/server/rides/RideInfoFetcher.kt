package server.rides

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import kotlinx.serialization.Serializable
import model.RideInfo
import model.enum.TransportType
import org.joda.time.DateTime
import server.NetworkInteractor
import server.TimeWorker.dateTimeFromString
import server.TimeWorker.isDateTimeAvailable
import server.rides.RideInfoFetcher.YandexRideInfo.RideInfoSegment
import utils.Utils

object RideInfoFetcher : RideInfoFetcherInterface {

    private const val YANDEX_API_CONFIG_FILE_NAME = "yandexapiconfig.properties"

    private const val YANDEX_API_CONFIG_API_KEY_NAME = "apikey"
    private const val YANDEX_API_FROM_KEY_NAME = "from"
    private const val YANDEX_API_TO_KEY_NAME = "to"
    private const val YANDEX_API_DATE_KEY_NAME = "date"
    private const val YANDEX_API_TRANSPORT_TYPES_KEY_NAME = "transport_types"
    private const val YANDEX_API_TRANSFERS_TYPES_KEY_NAME = "transfers"

    private const val YANDEX_API_BASE_URL = "https://api.rasp.yandex.net/v3.0/search/"
    private const val YANDEX_API_BALTIYSKY_RAILWAY_STATION_CODE = "s9602498"
    private const val YANDEX_API_UNIVERSITY_STATION_CODE = "s9603770"
    private const val YANDEX_API_TRANSFER = "false"

    private lateinit var yandexServiceApiKey: String

    fun init() {
        yandexServiceApiKey = Utils.getProperty(YANDEX_API_CONFIG_FILE_NAME, YANDEX_API_CONFIG_API_KEY_NAME)
    }

    override suspend fun getDormitoryToTownRides(date: DateTime, transportType: TransportType): List<RideInfo> {
        val response: YandexRideInfo =
            getStationsFromToInfo(
                YANDEX_API_UNIVERSITY_STATION_CODE,
                YANDEX_API_BALTIYSKY_RAILWAY_STATION_CODE,
                date,
                transportType
            ).body()
        return response.segments.filter { isDateTimeAvailable(dateTimeFromString(it.departure)) }.map {
            RideInfo(
                fromStationCode = it.from.code,
                toStationCode = it.to.code,
                departureAt = dateTimeFromString(it.departure),
                transportType = transportType,
            )
        }
    }

    override suspend fun getTownToDormitoryRides(date: DateTime, transportType: TransportType): List<RideInfo> {
        val response: YandexRideInfo =
            getStationsFromToInfo(
                YANDEX_API_BALTIYSKY_RAILWAY_STATION_CODE,
                YANDEX_API_UNIVERSITY_STATION_CODE,
                date,
                transportType
            ).body()
        return response.segments.filter { isDateTimeAvailable(dateTimeFromString(it.departure)) }.map {
            RideInfo(
                fromStationCode = it.from.code,
                toStationCode = it.to.code,
                departureAt = dateTimeFromString(it.departure),
                transportType = transportType,
            )
        }
    }

    private suspend fun getStationsFromToInfo(from: String, to: String, date: DateTime, transport_type: TransportType): HttpResponse {
        return NetworkInteractor.get(
            YANDEX_API_BASE_URL,
            listOf(HttpHeaders.Authorization to yandexServiceApiKey),
            listOf(
                YANDEX_API_FROM_KEY_NAME to from,
                YANDEX_API_TO_KEY_NAME to to,
                YANDEX_API_DATE_KEY_NAME to date.toString(),
                YANDEX_API_TRANSPORT_TYPES_KEY_NAME to transport_type.transportName,
                YANDEX_API_TRANSFERS_TYPES_KEY_NAME to YANDEX_API_TRANSFER
            )
        )
    }

    private fun isRideYetAvailable(rideInfoSegment: RideInfoSegment): Boolean {
        return isDateTimeAvailable(dateTimeFromString(rideInfoSegment.departure))
    }

    /**
     * Ride opportunities for
     * https://yandex.ru/dev/rasp/doc/reference/schedule-point-point.html
     * request.
     */
    @Serializable
    internal data class YandexRideInfo(val segments: List<RideInfoSegment>) {

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
}
