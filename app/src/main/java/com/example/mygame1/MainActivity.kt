package com.example.mygame1

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.mygame1.utilities.SignalManager

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var gameBoard: GridLayout
    private lateinit var cats: Array<ImageView>
    private lateinit var hearts: Array<ImageView>
    private lateinit var matrix: Array<Array<ImageView>>

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private val handler = Handler(Looper.getMainLooper())
    private val gameManager = GameManager()
    private var isRunning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        gameBoard = findViewById(R.id.game_board)

        cats = arrayOf(
            findViewById(R.id.cat1),
            findViewById(R.id.cat2),
            findViewById(R.id.cat3),
            findViewById(R.id.cat4),
            findViewById(R.id.cat5)
        )

        hearts = arrayOf(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3)
        )

        for (cat in cats) cat.visibility = View.INVISIBLE
        updateCatPosition()

        if (GameSettings.speedMode != GameSettings.SpeedMode.SENSOR) {
            findViewById<View>(R.id.buttonLeft).setOnClickListener {
                gameManager.moveLeft()
                updateCatPosition()
            }
            findViewById<View>(R.id.buttonRight).setOnClickListener {
                gameManager.moveRight()
                updateCatPosition()
            }
        } else {
            findViewById<View>(R.id.buttonLeft).visibility = View.INVISIBLE
            findViewById<View>(R.id.buttonRight).visibility = View.INVISIBLE
        }

        setupMatrix()
        startGameLoop()
    }

    private fun setupMatrix() {
        matrix = Array(5) { col ->
            Array(9) { row ->
                val id = resources.getIdentifier("cell_${col}_${row}", "id", packageName)
                findViewById<ImageView>(id).apply { visibility = View.INVISIBLE }
            }
        }
    }

    private fun startGameLoop() {
        handler.post(object : Runnable {
            override fun run() {
                if (!isRunning) return

                val col = (0..4).random()
                val isBomb = (0..1).random() == 0
                dropItem(col, isBomb)

                val delay = when (GameSettings.speedMode) {
                    GameSettings.SpeedMode.FAST -> 500L
                    GameSettings.SpeedMode.SLOW -> 1000L
                    GameSettings.SpeedMode.SENSOR -> 700L
                }
                handler.postDelayed(this, delay)
            }
        })
    }

    private fun dropItem(col: Int, isBomb: Boolean) {
        var row = 0
        val dropHandler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                if (row > 0) matrix[col][row - 1].visibility = View.INVISIBLE

                if (row == 8) {
                    matrix[col][row].visibility = View.VISIBLE
                    matrix[col][row].setImageResource(
                        if (isBomb) R.drawable.bomb else R.drawable.fish
                    )

                    if (col == gameManager.catCol) {
                        matrix[col][row].visibility = View.INVISIBLE
                        if (isBomb) handleCollision() else handleScore()
                        return
                    } else {
                        dropHandler.postDelayed({
                            matrix[col][row].visibility = View.INVISIBLE
                        }, 300)
                        return
                    }
                }

                matrix[col][row].visibility = View.VISIBLE
                matrix[col][row].setImageResource(
                    if (isBomb) R.drawable.bomb else R.drawable.fish
                )
                row++
                dropHandler.postDelayed(this, 300)
            }
        }

        dropHandler.post(runnable)
    }

    private fun handleCollision() {
        gameManager.loseLife()
        SignalManager.vibrate(this)
        SignalManager.toast(this, "נפגעת!")
        updateHearts()
        if (gameManager.isGameOver()) goToGameOverScreen()
    }

    private fun handleScore() {
        gameManager.addScore()
        SignalManager.toast(this, "יאמי!")
    }

    private fun goToGameOverScreen() {
        isRunning = false
        startActivity(Intent(this, GameOverActivity::class.java))
        finish()
    }

    private fun updateCatPosition() {
        for (i in cats.indices) {
            cats[i].visibility = if (i == gameManager.catCol) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun updateHearts() {
        when (gameManager.lives) {
            2 -> hearts[2].visibility = View.INVISIBLE
            1 -> hearts[1].visibility = View.INVISIBLE
            0 -> hearts[0].visibility = View.INVISIBLE
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (GameSettings.speedMode != GameSettings.SpeedMode.SENSOR) return
        val x = event?.values?.get(0) ?: return
        if (x > 3) {
            gameManager.moveLeft()
            updateCatPosition()
        } else if (x < -3) {
            gameManager.moveRight()
            updateCatPosition()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}
