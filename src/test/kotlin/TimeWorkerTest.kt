import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import model.Application
import model.RideInfo
import model.enum.TransportType.SUBURBAN
import server.TimeWorker
import utils.Utils.generateNewUUID
import utils.customJson
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TimeWorkerTest {

    /**
     * Don't use bare DateTime, because serializer for him won't be found
     */
    @Test
    fun testDateTimeSerialization() {
        val departureExpected = TimeWorker.now(TimeWorker.ZONE_MOSCOW)
        val applicationExpected =
            Application.createNewApplication(
                generateNewUUID(), "Comment", RideInfo(
                    "A", "B", departureExpected, SUBURBAN,
                ), TimeWorker.ZONE_MOSCOW
            )
        val json = customJson.encodeToString(applicationExpected)
        val applicationActual = customJson.decodeFromString<Application>(json)
        val departureActual = applicationActual.rideInfo.departureAt
        assertEquals(departureExpected, departureActual)
    }
}
