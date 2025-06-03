package com.example.mygame1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mygame1.databinding.ActivityMenuBinding
import com.example.mygame1.utilities.ImageLoader
import com.example.mygame1.utilities.BackgroundMusicPlayer

class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        initListeners()

        BackgroundMusicPlayer.init(this).setResourceId(R.raw.background_music)
    }

    private fun initViews() {
        ImageLoader.loadImage(
            this,
            "https://sdmntprwestus2.oaiusercontent.com/files/00000000-ab3c-61f8-94e2-4e83e1d50e8d/raw?se=2025-06-03T16%3A26%3A18Z&sp=r&sv=2024-08-04&sr=b&scid=3fc30c66-2e3c-5472-bbe0-2875aeb9a30c&skoid=30ec2761-8f41-44db-b282-7a0f8809659b&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2025-06-03T00%3A09%3A08Z&ske=2025-06-04T00%3A09%3A08Z&sks=b&skv=2024-08-04&sig=Ble%2B5%2Bc0XamyLfLXxDIks/8ov3oulyoV4US2VrOSnC0%3D",
            binding.sensorsIMGBackground
        )
    }

    private fun initListeners() {

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
        binding.btnHighScores.setOnClickListener {
            val intent = Intent(this, GameOverActivity::class.java)
            intent.putExtra("SCORE", -1)
            startActivity(intent)
        }
    }

    private fun startGame() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        BackgroundMusicPlayer.getInstance().playMusic()
    }

    override fun onPause() {
        super.onPause()
        BackgroundMusicPlayer.getInstance().pauseMusic()
    }
}
