package server.rides

import org.joda.time.LocalDate

interface RideInfoFetcherInterface {
    suspend fun getDormitoryToTownRides(date: LocalDate): List<RideInfo>
    suspend fun getTownToDormitoryRides(date: LocalDate): List<RideInfo>
}