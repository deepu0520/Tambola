package com.newitzone.tambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.tambola.R
import kotlinx.android.synthetic.main.cardview_open_random_number.view.*
import kotlinx.android.synthetic.main.cardview_ticket_number.view.*
import kotlinx.android.synthetic.main.cardview_tournament_item.view.*

class TicketNumberAdapter(val items : List<Int>, val context: Context) : RecyclerView.Adapter<ViewHolderTicketNum>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTicketNum {
        return ViewHolderTicketNum(LayoutInflater.from(context).inflate(R.layout.cardview_ticket_number, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderTicketNum, position: Int) {
        if (items[position] == 0){
            holder?.tvNum?.text = ""
        }else{
            holder?.tvNum?.text = items[position].toString()
        }
        holder.rL.setOnClickListener {

        }
    }
}

class ViewHolderTicketNum (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvNum = view.text_num
    val rL = view.relative_num
}