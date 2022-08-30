package utils

import com.google.common.io.Resources
import io.ktor.client.call.body
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import server.NetworkInteractor
import java.io.InputStream
import java.util.*

object Utils {
    private fun getResourcesFile(fileName: String): InputStream? {
        return Resources.getResource(fileName).openStream()
    }

    fun getProperty(fileName: String, propertyName: String): String {
        val prop = Properties()
        prop.load(getResourcesFile(fileName))
        return prop.getProperty(propertyName)
    }

    fun generateNewUUID(): UUID {
        return UUID.randomUUID()
    }

    fun textIsCommand(text: String?): Boolean {
        return !text.isNullOrBlank() && text[0] == '/'
    }

    fun downloadFileFromChat(botToken: String, fileId: String) {
        runBlocking {
            val getFileUrl = "https://api.telegram.org/bot$botToken/getFile?file_id=$fileId"
            CustomLogger.logInfoMessage("Get file url = $getFileUrl")
            val fileResult: FileResult = NetworkInteractor.get(getFileUrl).body()
            val filePath = fileResult.filePath
            val downloadFileUrl = "https://api.telegram.org/file/bot$botToken/$filePath"
            NetworkInteractor.downloadFile(downloadFileUrl)
        }
    }
}

@Serializable
data class GetFileInfo(val result: FileResult)

@Serializable
data class FileResult(val filePath: String)
