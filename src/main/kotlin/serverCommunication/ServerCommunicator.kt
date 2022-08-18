package serverCommunication

import java.io.BufferedReader
import java.io.InputStreamReader

object ServerCommunicator {
    // see https://stackoverflow.com/questions/11774887/how-to-stop-mongo-db-in-one-command
    // to find how to stop mongodb server
    fun runMongoDb() {
        ProcessBuilder("mongod").start()
    }

    fun isMongodbRunning(): Boolean {
        val process = ProcessBuilder("systemctl", "status", "mongod").start()
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            if (line!!.contains("inactive")) {
                return false
            }
        }
        return true
    }
}
