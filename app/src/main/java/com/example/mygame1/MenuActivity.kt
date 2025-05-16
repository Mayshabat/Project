package com.example.mygame1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mygame1.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startGameButton.setOnClickListener {
            val useSensor = binding.sensorSwitch.isChecked

            GameSettings.speedMode = if (useSensor) {
                GameSettings.SpeedMode.SENSOR
            } else {
                val isFast = binding.speedSwitch.isChecked
                if (isFast) GameSettings.SpeedMode.FAST else GameSettings.SpeedMode.SLOW
            }

            startGame()
        }
    }

    private fun startGame() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
