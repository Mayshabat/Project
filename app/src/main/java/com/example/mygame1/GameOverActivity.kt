
package com.example.mygame1
import android.util.Log

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mygame1.interfaces.Callback_HighScoreItemClicked
import com.example.mygame1.ui.theme.HighScoreFragment
import com.example.mygame1.ui.theme.MapFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.Manifest
import android.content.pm.PackageManager

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


        // טעינת המפה בתחתית המסך
        mapFragment = MapFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_FRAME_map, mapFragment)
            .commit()

        // טעינת טבלת שיאים עם callback בעת לחיצה
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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        saveCurrentLocationAndScore(score)
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
            if (location != null) {
                saveScoreToPrefs(this, score, location.latitude, location.longitude)
            } else {
                Toast.makeText(this, "מיקום נוכחי לא זמין", Toast.LENGTH_SHORT).show()
                saveScoreToPrefs(this, score, 0.0, 0.0)
            }
        }
    }

    companion object {
        fun saveScoreToPrefs(context: Context, score: Int, lat: Double, lon: Double) {
            val prefs = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE)
            val list = getSavedScores(context).toMutableList()



            list.add(Triple("", score, Pair(lat, lon)))



            val sortedList = list.sortedByDescending { it.second }.take(10)
            val json = Gson().toJson(sortedList)
            prefs.edit().putString("score_list", json).apply()
        }

        fun getSavedScores(context: Context): List<Triple<String, Int, Pair<Double, Double>>> {
            val prefs = context.getSharedPreferences("high_scores", Context.MODE_PRIVATE)
            val json = prefs.getString("score_list", "[]")

            return try {
                val type = object : TypeToken<List<Triple<String, Int, Pair<Double, Double>>>>() {}.type
                Gson().fromJson(json, type)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    }
}