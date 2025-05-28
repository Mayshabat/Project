
package com.example.mygame1.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mygame1.GameOverActivity
import com.example.mygame1.R
import com.example.mygame1.adapters.HighScoreAdapter
import com.example.mygame1.interfaces.Callback_HighScoreItemClicked

class HighScoreFragment : Fragment() {

    var highScoreItemClicked: Callback_HighScoreItemClicked? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_high_score, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_high_scores)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val scores = GameOverActivity.getSavedScores(requireContext())
            .sortedByDescending { it.second }

        recyclerView.adapter = HighScoreAdapter(scores, object : Callback_HighScoreItemClicked {
            override fun highScoreItemClicked(lat: Double, lon: Double) {
                highScoreItemClicked?.highScoreItemClicked(lat, lon)
            }
        })
        return view
    }
}
