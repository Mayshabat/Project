package com.example.mygame1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mygame1.R
import com.example.mygame1.interfaces.Callback_HighScoreItemClicked

class HighScoreAdapter(
    private val scores: List<Triple<String, Int, Pair<Double, Double>>>,
    private val callback: Callback_HighScoreItemClicked
) : RecyclerView.Adapter<HighScoreAdapter.HighScoreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighScoreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_high_score, parent, false)
        return HighScoreViewHolder(view)
    }

    override fun getItemCount(): Int = scores.size

    override fun onBindViewHolder(holder: HighScoreViewHolder, position: Int) {
        val scoreData = scores[position]
        val score = scoreData.second
        val lat = scoreData.third.first
        val lon = scoreData.third.second

        holder.rankText.text = "#${position + 1}"
        holder.scoreText.text = score.toString()

        holder.itemView.setOnClickListener {
            callback.highScoreItemClicked(lat, lon)
        }
    }

    class HighScoreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rankText: TextView = view.findViewById(R.id.score_item_place)
        val scoreText: TextView = view.findViewById(R.id.score_item_score)
    }
}
