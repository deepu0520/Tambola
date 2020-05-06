package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.R
import kotlinx.android.synthetic.main.cardview_open_random_number.view.*

class RandomNumberAdapter(val items : MutableList<String>, val context: Context) : RecyclerView.Adapter<ViewHolderRN>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRN {
        return ViewHolderRN(LayoutInflater.from(context).inflate(R.layout.cardview_open_random_number, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderRN, position: Int) {
        holder?.tvRanNum?.text = items.get(position)
    }
}

class ViewHolderRN (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvRanNum = view.text_ran_num
}