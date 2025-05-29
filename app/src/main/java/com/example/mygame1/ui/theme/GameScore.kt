package com.example.mygame1.ui.theme

data class GameScore(
    val score: Int,
    val timestamp: Long,
    val lat: Double ,
    val lon: Double
) {
    fun toStorageString(): String {
        return "$score|$timestamp|$lat|$lon"
    }

    companion object {
        fun fromStorageString(data: String): GameScore? {
            val parts = data.split("|")
            return if (parts.size == 4) {
                val score = parts[0].toIntOrNull()
                val timestamp = parts[1].toLongOrNull()
                val lat = parts[2].toDoubleOrNull()
                val lon = parts[3].toDoubleOrNull()

                if (score != null && timestamp != null && lat != null && lon != null) {
                    GameScore(score, timestamp, lat, lon)
                } else null
            } else null
        }
    }
}