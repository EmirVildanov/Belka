package server

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.serialization.json.Json
import java.io.File

object NetworkInteractor {
    private lateinit var client: HttpClient

    fun init() {
        client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    suspend fun get(
        url: String,
        requestHeaders: List<Pair<String, String>> = listOf(),
        requestParameters: List<Pair<String, String>> = listOf()
    ): HttpResponse {
        return client.get(url) {
            headers {
                requestHeaders.forEach { header ->
                    append(header.first, header.second)
                }
            }
            url {
                requestParameters.forEach { parameter ->
                    parameters.append(parameter.first, parameter.second)
                }
            }
        }
    }

    suspend fun downloadFile(urlString: String) {
        val url = Url(urlString)
        val file = File(url.pathSegments.last())
        client.get(url).bodyAsChannel().copyAndClose(file.writeChannel())
    }

    fun stop() {
        client.close()
    }
}