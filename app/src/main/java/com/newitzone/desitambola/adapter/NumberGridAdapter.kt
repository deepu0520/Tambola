package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.R
import com.newitzone.desitambola.databinding.CardviewNumberGridBinding
import model.NumModel

class NumberGridAdapter(val items : MutableList<NumModel>, val context: Context) : RecyclerView.Adapter<ViewHolderNG>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderNG {
        val binding =
            CardviewNumberGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderNG(binding)
       // return ViewHolderNG(LayoutInflater.from(context).inflate(R.layout.cardview_number_grid, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderNG, position: Int) {
        holder?.tvNum?.text = items[position].num.toString()
        if (items[position].isChecked){
            holder?.tvNum?.setBackgroundResource(R.color.color_yellow)
        }
    }
}

class ViewHolderNG(view: CardviewNumberGridBinding) : RecyclerView.ViewHolder(view.root) {
    // Holds the TextView that will add each animal to
    val tvNum = view.textNum
}