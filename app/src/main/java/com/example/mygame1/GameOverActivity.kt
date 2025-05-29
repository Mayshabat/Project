package com.example.mygame1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mygame1.interfaces.Callback_HighScoreItemClicked
import com.example.mygame1.models.PlayerRecord
import com.example.mygame1.ui.theme.HighScoreFragment
import com.example.mygame1.ui.theme.MapFragment
import com.example.mygame1.utilities.ScoreStorage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

class GameOverActivity : AppCompatActivity() {

    private lateinit var btnRestart: Button
    private lateinit var mapFragment: MapFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        score = intent.getIntExtra("SCORE", 0)
        btnRestart = findViewById(R.id.back_to_home_button)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // טען את המפה
        mapFragment = MapFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_FRAME_map, mapFragment)
            .commit()

        // טען את טבלת השיאים
        val highScoreFragment = HighScoreFragment()
        highScoreFragment.highScoreItemClicked = object : Callback_HighScoreItemClicked {
            override fun highScoreItemClicked(lat: Double, lon: Double) {
                mapFragment.showMarkerAtLocation(lat, lon)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_FRAME_list, highScoreFragment)
            .commit()

        if (score != -1) {
            saveCurrentLocationAndScore(score)
        }

        btnRestart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun saveCurrentLocationAndScore(score: Int) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            val lat = location?.latitude ?: 0.0
            val lon = location?.longitude ?: 0.0
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            val record = PlayerRecord(date, score, Pair(lat, lon))
            ScoreStorage.save(this, record)

            // רק אחרי ששמרת — טען את הפרגמנט
            val highScoreFragment = HighScoreFragment()
            highScoreFragment.highScoreItemClicked = object : Callback_HighScoreItemClicked {
                override fun highScoreItemClicked(lat: Double, lon: Double) {
                    mapFragment.showMarkerAtLocation(lat, lon)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_FRAME_list, highScoreFragment)
                .commit()
        }}
}
