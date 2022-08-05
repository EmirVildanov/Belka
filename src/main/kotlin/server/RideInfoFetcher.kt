package server

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import model.RideInfo
import model.RideOpportunitiesInfo
import model.enum.TransportType
import org.joda.time.DateTime
import org.joda.time.LocalDate
import utils.Utils

object RideInfoFetcher {

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

    private var yandexServiceApiKey: String = Utils.getProperty(YANDEX_API_CONFIG_FILE_NAME, YANDEX_API_CONFIG_API_KEY_NAME)
    private var client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getDormitoryToTownRides(date: LocalDate): List<RideInfo> {
        val response: RideOpportunitiesInfo =
            getFromToInfo(YANDEX_API_UNIVERSITY_STATION_CODE, YANDEX_API_BALTIYSKY_RAILWAY_STATION_CODE, date).body()
        return response.segments.filter { DateTime(it.departure).toDateTime() > DateTime.now().toDateTime() }
    }

    suspend fun getTownToDormitoryRides(date: LocalDate): List<RideInfo> {
        val response: RideOpportunitiesInfo =
            getFromToInfo(YANDEX_API_BALTIYSKY_RAILWAY_STATION_CODE, YANDEX_API_UNIVERSITY_STATION_CODE, date).body()
        return response.segments.filter { DateTime(it.departure).toDateTime() > DateTime.now().toDateTime() }
    }

    private suspend fun getFromToInfo(from: String, to: String, date: LocalDate): HttpResponse {
        return client.get(YANDEX_API_BASE_URL) {
            headers {
                append(HttpHeaders.Authorization, yandexServiceApiKey)
            }
            url {
                parameters.append(YANDEX_API_FROM_KEY_NAME, from)
                parameters.append(YANDEX_API_TO_KEY_NAME, to)
                parameters.append(YANDEX_API_DATE_KEY_NAME, date.toString())
                parameters.append(YANDEX_API_TRANSPORT_TYPES_KEY_NAME, TransportType.SUBURBAN.transportName)
                parameters.append(YANDEX_API_TRANSFERS_TYPES_KEY_NAME, YANDEX_API_TRANSFER)
            }
        }
    }
}