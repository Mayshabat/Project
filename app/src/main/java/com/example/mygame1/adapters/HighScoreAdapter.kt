package com.example.mygame1.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mygame1.R
import com.example.mygame1.interfaces.Callback_HighScoreItemClicked
import com.example.mygame1.utilities.ScoreStorage
import com.example.mygame1.models.PlayerRecord

class HighScoreAdapter(
    private val records: List<PlayerRecord>,
    private val callback: Callback_HighScoreItemClicked
) : RecyclerView.Adapter<HighScoreAdapter.RecordHolder>() {

    class RecordHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pos: TextView = view.findViewById(R.id.score_item_place)
        val score: TextView = view.findViewById(R.id.score_item_score)
        val date: TextView = view.findViewById(R.id.score_item_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_high_score, parent, false)
        return RecordHolder(view)
    }

    override fun onBindViewHolder(holder: RecordHolder, position: Int) {
        val item = records[position]
        holder.pos.text = (position + 1).toString()
        holder.score.text = item.score.toString()
        holder.date.text = item.date
        holder.itemView.setOnClickListener {
            callback.highScoreItemClicked(item.location.first, item.location.second)
        }
    }

    override fun getItemCount(): Int = records.size
}