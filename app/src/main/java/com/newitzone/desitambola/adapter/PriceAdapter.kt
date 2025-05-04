package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.R
import com.newitzone.desitambola.databinding.CardviewGamesPriceBinding
import com.newitzone.desitambola.databinding.CardviewNumberGridBinding


class PriceAdapter(val items : List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            CardviewGamesPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
      //  return ViewHolder(LayoutInflater.from(context).inflate(R.layout.cardview_games_price, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.tvPrice?.text = "₹"+items.get(position)
    }
}

class ViewHolder (view: CardviewGamesPriceBinding) : RecyclerView.ViewHolder(view.root) {
    // Holds the TextView that will add each animal to
    val tvPrice = view.textPrice
}