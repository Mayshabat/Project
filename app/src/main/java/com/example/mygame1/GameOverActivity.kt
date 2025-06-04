package com.example.mygame1

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mygame1.interfaces.Callback_HighScoreItemClicked
import com.example.mygame1.models.PlayerRecord
import com.example.mygame1.ui.theme.HighScoreFragment
import com.example.mygame1.ui.theme.MapFragment
import com.example.mygame1.utilities.ScoreStorage
import com.google.android.gms.location.*
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


        mapFragment = MapFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_FRAME_map, mapFragment)
            .commit()


        loadHighScoreFragment()


        if (score != -1) {
            getCurrentLocationAndSave(score)
        }

        btnRestart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loadHighScoreFragment() {
        val highScoreFragment = HighScoreFragment()
        highScoreFragment.highScoreItemClicked = object : Callback_HighScoreItemClicked {
            override fun highScoreItemClicked(lat: Double, lon: Double) {
                mapFragment.showMarkerAtLocation(lat, lon)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_FRAME_list, highScoreFragment)
            .commit()
    }

//
private fun getCurrentLocationAndSave(score: Int) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1001
        )
        return
    }

    val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 1000
        numUpdates = 1
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val location: Location? = locationResult.lastLocation
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val lat = location?.latitude ?: 0.0
            val lon = location?.longitude ?: 0.0

            val record = PlayerRecord(date, score, Pair(lat, lon))
            ScoreStorage.save(this@GameOverActivity, record)


            loadHighScoreFragment()
        }
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
}


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocationAndSave(score)
        }
    }
}
