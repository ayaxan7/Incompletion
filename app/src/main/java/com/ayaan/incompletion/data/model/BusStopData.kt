package com.ayaan.incompletion.data.model

data class BusStopOption(
    val id: String,
    val name: String
)

object BusStopData {
    val busStops = mapOf(
        "S1" to "Kempegowda Bus Station",
        "S2" to "Corporation",
        "S3" to "Town Hall",
        "S4" to "Krishnarajendra Market",
        "S5" to "Makkala Koota",
        "S6" to "Mahila Samaja",
        "S7" to "National College",
        "S8" to "Basavanagudi Police Station",
        "S9" to "Tata Silk Farm",
        "S10" to "M M Industries",
        "S11" to "Shasthri Bekary",
        "S12" to "Monotype",
        "S13" to "Cauvery Nagara",
        "S14" to "Yarab Nagara",
        "S15" to "Kadirenahalli Cross",
        "S16" to "Dayananda Sagar College",
        "S17" to "Maharani College",
        "S18" to "KR Circle",
        "S19" to "Kumaraswamy Layout Police Station",
        "S20" to "Kumaraswamy Layout",
        "S21" to "Canara Bank",
        "S22" to "ISRO Layout",
        "S23" to "Kumaraswamy Layout 2nd Stage",
        "S24" to "Water Tank Kumaraswamy Layout",
        "S25" to "Cauvery Bhavana"
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
