package server.rides

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import model.RideInfo
import model.RideOpportunitiesInfo
import model.enum.TransportType.SUBURBAN
import org.joda.time.DateTime
import org.joda.time.LocalDate
import server.NetworkInteractor
import utils.Utils

object RideInfoFetcher : RidesInfoFetcherInterface {

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

    override suspend fun getDormitoryToTownRides(date: LocalDate): List<RideInfo> {
        val response: RideOpportunitiesInfo =
            getStationsFromToInfo(
                YANDEX_API_UNIVERSITY_STATION_CODE,
                YANDEX_API_BALTIYSKY_RAILWAY_STATION_CODE,
                date
            ).body()
        return response.segments.filter { DateTime(it.departure).toDateTime() > DateTime.now().toDateTime() }
    }

    override suspend fun getTownToDormitoryRides(date: LocalDate): List<RideInfo> {
        val response: RideOpportunitiesInfo =
            getStationsFromToInfo(
                YANDEX_API_BALTIYSKY_RAILWAY_STATION_CODE,
                YANDEX_API_UNIVERSITY_STATION_CODE,
                date
            ).body()
        return response.segments.filter { DateTime(it.departure).toDateTime() > DateTime.now().toDateTime() }
    }

    private suspend fun getStationsFromToInfo(from: String, to: String, date: LocalDate): HttpResponse {
        return NetworkInteractor.get(
            YANDEX_API_BASE_URL,
            listOf(HttpHeaders.Authorization to yandexServiceApiKey),
            listOf(
                YANDEX_API_FROM_KEY_NAME to from,
                YANDEX_API_TO_KEY_NAME to to,
                YANDEX_API_DATE_KEY_NAME to date.toString(),
                YANDEX_API_TRANSPORT_TYPES_KEY_NAME to SUBURBAN.transportName,
                YANDEX_API_TRANSFERS_TYPES_KEY_NAME to YANDEX_API_TRANSFER
            )
        )
    }
}
