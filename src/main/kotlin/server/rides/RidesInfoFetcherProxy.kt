package server.rides

import org.joda.time.LocalDate

class RidesInfoFetcherProxy : RidesInfoFetcherInterface {
    override suspend fun getDormitoryToTownRides(date: LocalDate): List<RideInfo> {
        return RideInfoFetcher.getDormitoryToTownRides(date)
    }

    override suspend fun getTownToDormitoryRides(date: LocalDate): List<RideInfo> {
        return RideInfoFetcher.getTownToDormitoryRides(date)
    }
}