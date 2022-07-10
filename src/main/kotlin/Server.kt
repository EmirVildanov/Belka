import utils.getResourcesFile
import java.util.*

class Server {

    private fun readProperties() {
        val prop = Properties()
        prop.load(getResourcesFile("telegramconfig.properties"))
        val accessToken = prop.getProperty("apiToken")
        println(accessToken)
    }

    fun start() {
        try {
            readProperties()
        } catch (e: Exception) {
//            println(e.message)
            throw e
        }
    }
}