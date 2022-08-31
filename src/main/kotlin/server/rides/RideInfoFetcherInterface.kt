package server.rides

import model.RideInfo
import model.enum.TransportType
import org.joda.time.DateTime

interface RideInfoFetcherInterface {
    suspend fun getDormitoryToTownRides(date: DateTime, transportType: TransportType): List<RideInfo>
    suspend fun getTownToDormitoryRides(date: DateTime, transportType: TransportType): List<RideInfo>
}