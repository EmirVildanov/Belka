import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.StationInfo
import model.SucceededRide
import org.joda.time.DateTime
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TimeWorkerTest {

    @Test
    fun testSerialization() {
        val dateTime = DateTime.now()
        val rideExpected = SucceededRide(1, dateTime, StationInfo("A"), StationInfo("B"), listOf(1, 2))
        val json = Json.encodeToString(rideExpected)
        val rideActual = Json.decodeFromString<SucceededRide>(json)
        println(rideActual.dateTime == dateTime)
        assertEquals(rideExpected, rideActual)
    }
}