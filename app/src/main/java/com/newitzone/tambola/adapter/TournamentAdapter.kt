package com.newitzone.tambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.tambola.R
import kotlinx.android.synthetic.main.cardview_tournament_item.view.*

class TournamentAdapter(val items : List<String>, val context: Context) : RecyclerView.Adapter<ViewHolderTournament>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTournament {
        return ViewHolderTournament(LayoutInflater.from(context).inflate(R.layout.cardview_tournament_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderTournament, position: Int) {
        holder?.tvTitle?.text = items.get(position)
    }
}

class ViewHolderTournament (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvTitle = view.text_title
}