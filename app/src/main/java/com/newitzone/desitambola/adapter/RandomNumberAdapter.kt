package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.databinding.CardviewOpenRandomNumberBinding

class RandomNumberAdapter(val items: MutableList<String>, val context: Context) :
    RecyclerView.Adapter<ViewHolderRN>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRN {
        val binding =
            CardviewOpenRandomNumberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolderRN(binding)
        // return ViewHolderRN(LayoutInflater.from(context).inflate(R.layout.cardview_open_random_number, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderRN, position: Int) {
        holder?.tvRanNum?.text = items.get(position)
    }
}

class ViewHolderRN(view: CardviewOpenRandomNumberBinding) : RecyclerView.ViewHolder(view.root) {
    // Holds the TextView that will add each animal to
    val tvRanNum = view.textRanNum
}