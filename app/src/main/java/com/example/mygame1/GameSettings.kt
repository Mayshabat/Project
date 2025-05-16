package com.example.mygame1

object GameSettings {
    enum class SpeedMode {
        SLOW, FAST, SENSOR
    }

    var speedMode: SpeedMode = SpeedMode.SLOW
}
