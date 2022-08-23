package server.rides

import model.RideInfo
import org.joda.time.LocalDate

interface RidesInfoFetcherInterface {
    suspend fun getDormitoryToTownRides(date: LocalDate): List<RideInfo>
    suspend fun getTownToDormitoryRides(date: LocalDate): List<RideInfo>
}