package model.enum

enum class TransportType(val transportName: String) {
    SUBURBAN("suburban"),
    BUS("bus"),

    /**
     * Can not pass it to RideInfoFetchcer as Yandex does not support it.
     */
    PEDESTRIAN("pedestrian"),
}
