package com.example.mygame1.utilities

import android.content.Context
import com.example.mygame1.models.PlayerRecord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ScoreStorage {
    private const val PREF_KEY = "high_scores"
    private const val KEY_LIST = "score_list"

    fun save(context: Context, record: PlayerRecord) {
        val prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
        val current = getAll(context).toMutableList()
        current.add(record)
        val sorted = current.sortedByDescending { it.score }.take(10)
        prefs.edit().putString(KEY_LIST, Gson().toJson(sorted)).apply()
    }

    fun getAll(context: Context): List<PlayerRecord> {
        val prefs = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LIST, "[]")
        val type = object : TypeToken<List<PlayerRecord>>() {}.type
        return Gson().fromJson(json, type)
    }
}
