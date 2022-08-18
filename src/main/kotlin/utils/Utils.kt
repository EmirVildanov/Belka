package utils

import com.google.common.io.Resources
import io.ktor.client.call.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import server.NetworkInteractor
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

object Utils {
    private const val RATE_MIN_VALUE = 1
    private const val RATE_MAX_VALUE = 10
    private const val TEXT_FEEDBACK_SYMBOLS_BOUND = 150

    private fun getResourcesFile(fileName: String): InputStream? {
        return Resources.getResource(fileName).openStream()
    }

    fun getProperty(fileName: String, propertyName: String): String {
        val prop = Properties()
        prop.load(getResourcesFile(fileName))
        return prop.getProperty(propertyName)
    }

    fun checkRateInBounds(rate: Int): Boolean {
        return rate in RATE_MIN_VALUE..RATE_MAX_VALUE;
    }

    fun checkTextInBounds(text: String): Boolean {
        return text.length <= TEXT_FEEDBACK_SYMBOLS_BOUND;
    }

    fun textIsCommand(text: String?): Boolean {
        return !text.isNullOrBlank() && text[0] == '/'
    }

    fun downloadFileFromChat(botToken: String, fileId: String) {
        runBlocking {
            val getFileUrl = "https://api.telegram.org/bot$botToken/getFile?file_id=$fileId"
            CustomLogger.logInfoMessage("Get file url = $getFileUrl")
            val fileResult: FileResult = NetworkInteractor.get(getFileUrl).body()
            val filePath = fileResult.file_path
            val downloadFileUrl = "https://api.telegram.org/file/bot$botToken/$filePath"
            NetworkInteractor.downloadFile(downloadFileUrl)
        }
    }
}

@Serializable
data class GetFileInfo(val result: FileResult)

@Serializable
data class FileResult(val file_path: String)
