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
        BackgroundMusicPlayer.init(this).setResourceId(R.raw.background_music)
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
        intent.putExtra("score",-1)
        startActivity(intent)
        finish()
    }
    private fun initViews() {

        ImageLoader.loadImage(
            this,
            "https://sdmntprsouthcentralus.oaiusercontent.com/files/00000000-9140-61f7-8eaa-8ba4dd3893b0/raw?se=2025-05-20T08%3A58%3A03Z&sp=r&sv=2024-08-04&sr=b&scid=dcbcb7ff-ab97-582f-bd07-b273458c41bf&skoid=c953efd6-2ae8-41b4-a6d6-34b1475ac07c&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2025-05-20T06%3A58%3A26Z&ske=2025-05-21T06%3A58%3A26Z&sks=b&skv=2024-08-04&sig=w5C88S2Dw4ae6mc9LTdSk3wyDxjjM/Wrkw45cSn76jM%3D",
            binding.sensorsIMGBackground

        )

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
