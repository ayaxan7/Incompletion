package com.ayaan.incompletion.data.model

data class BusStopOption(
    val id: String,
    val name: String
)

object BusStopData {
    val busStops = mapOf(
        "S1" to "Central Bus Station",
        "S2" to "City Mall",
        "S3" to "Railway Station",
        "S4" to "University Campus",
        "S5" to "Hospital Complex",
        "S6" to "Airport Terminal",
        "S7" to "Tech Park",
        "S8" to "Shopping Center",
        "S9" to "Government Office",
        "S10" to "Stadium",
        "S11" to "Beach Road",
        "S12" to "Industrial Area",
        "S13" to "Residential Zone A",
        "S14" to "Residential Zone B",
        "S15" to "Market Square",
        "S16" to "Business District",
        "S17" to "Old Town",
        "S18" to "New Town",
        "S19" to "Metro Junction",
        "S20" to "Convention Center",
        "S21" to "Sports Complex",
        "S22" to "Cultural Center",
        "S23" to "Financial District",
        "S24" to "Waterfront",
        "S25" to "Suburban Hub"
    )

    val busStopOptions: List<BusStopOption> = busStops.map { (id, name) ->
        BusStopOption(id = id, name = name)
    }

    fun getStopName(stopId: String): String {
        return busStops[stopId] ?: stopId
    }

    fun getStopId(stopName: String): String {
        return busStops.entries.find { it.value == stopName }?.key ?: stopName
    }
}
