import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import model.StationInfo
import model.SucceededRide
import org.joda.time.DateTime
import server.RedisConnector
import server.Server

fun main() {
//    val server = Server
//    server.start()
//    RedisConnector.start()

    val dateTime = DateTime.now()
    println(dateTime)
    val ride = SucceededRide(1, dateTime, StationInfo("AAA"), StationInfo("BBB"), listOf(1, 2))
    val json = Json.encodeToString(ride)
    println(json)
    val rido = Json.decodeFromString<SucceededRide>(json)
    println(rido.dateTime == dateTime)
}
