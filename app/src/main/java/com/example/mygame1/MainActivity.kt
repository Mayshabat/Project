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
import androidx.appcompat.widget.AppCompatImageView
import com.example.mygame1.utilities.SignalManager
import com.google.android.material.textview.MaterialTextView
import com.example.mygame1.utilities.ImageLoader
import com.example.mygame1.utilities.SingleSoundPlayer
import com.example.mygame1.utilities.BackgroundMusicPlayer
import com.bumptech.glide.Glide

// MainActivity.kt
class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var gameBoard: GridLayout
    private lateinit var cats: Array<ImageView>
    private lateinit var hearts: Array<ImageView>

    data class Cell(val fish: ImageView, val bomb: ImageView)
    private lateinit var matrix: Array<Array<Cell>>
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var main_LBL_score: MaterialTextView

    private val handler = Handler(Looper.getMainLooper())
    private val gameManager = GameManager()
    private var isRunning = true

    private lateinit var sensors_IMG_background: AppCompatImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BackgroundMusicPlayer.init(this).setResourceId(R.raw.background_music)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        main_LBL_score = findViewById(R.id.main_LBL_score)
        sensors_IMG_background = findViewById(R.id.sensors_IMG_background)
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
        initViews()
        setupMatrix()
        startGameLoop()
    }

    private fun initViews() {
        ImageLoader.loadImage(
            this,
            "https://free-vectors.net/_ph/6/805542910.jpg",
            sensors_IMG_background
        )
    }

    private fun setupMatrix() {
        matrix = Array(5) { col ->
            Array(9) { row ->
                val fishId = resources.getIdentifier("fish_${col}_${row}", "id", packageName)
                val bombId = resources.getIdentifier("bomb_${col}_${row}", "id", packageName)

                val fishView = findViewById<ImageView>(fishId)
                val bombView = findViewById<ImageView>(bombId)

                fishView.visibility = View.INVISIBLE
                bombView.visibility = View.INVISIBLE

                Cell(fishView, bombView)
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
                if (row > 0) {
                    matrix[col][row - 1].fish.visibility = View.INVISIBLE
                    matrix[col][row - 1].bomb.visibility = View.INVISIBLE
                }
                val cell = matrix[col][row]

                if (row == 8) {
                    if (col == gameManager.catCol) {
                        if (isBomb) {
                            cell.bomb.visibility = View.VISIBLE
                            handleCollision()
                        } else {
                            cell.fish.visibility = View.VISIBLE
                            handleScore()
                        }
                    } else {
                        if (isBomb) {
                            cell.bomb.visibility = View.VISIBLE
                        } else {
                            cell.fish.visibility = View.VISIBLE
                        }
                    }

                    dropHandler.postDelayed({
                        cell.bomb.visibility = View.INVISIBLE
                        cell.fish.visibility = View.INVISIBLE
                    }, 300)

                    return
                }

                if (isBomb) {
                    cell.bomb.visibility = View.VISIBLE
                } else {
                    cell.fish.visibility = View.VISIBLE
                }

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
        SingleSoundPlayer(this).playSound(R.raw.boom)

        updateHearts()
        if (gameManager.isGameOver()) goToGameOverScreen()
    }

    private fun handleScore() {
        gameManager.addScore(10)
        main_LBL_score.text = gameManager.score.toString()
        SignalManager.toast(this, "יאמי!")
    }

    private fun goToGameOverScreen() {
        isRunning = false
        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra("SCORE", gameManager.score)
        startActivity(intent)
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
        BackgroundMusicPlayer.getInstance().playMusic()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        BackgroundMusicPlayer.getInstance().pauseMusic()
    }
}
