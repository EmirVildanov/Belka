package server

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.*
import io.ktor.utils.io.copyAndClose
import kotlinx.serialization.json.Json
import java.io.File

object NetworkInteractor {
    var client: HttpClient? = null

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
        return client!!.get(url) {
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
        client!!.get(url).bodyAsChannel().copyAndClose(file.writeChannel())
    }

    fun stop() {
        if (client != null) {
            client!!.close()
        }
    }
}