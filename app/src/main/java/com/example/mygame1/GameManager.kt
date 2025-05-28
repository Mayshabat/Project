package com.example.mygame1

class GameManager(
    var lives: Int = 3,
    var catCol: Int = 1,
    var score:Int =0,
    private val maxCol: Int = 4
) {

    fun moveLeft() {
        if (catCol > 0) {
            catCol--
        }
    }

    fun moveRight() {
        if (catCol < maxCol) {
            catCol++
        }
    }

    fun loseLife() {
        if (lives > 0) {
            lives--
        }
    }

    fun isGameOver(): Boolean {
        return lives <= 0
    }

    fun addScore(points: Int = 1) {
        score+=points
    }
}

