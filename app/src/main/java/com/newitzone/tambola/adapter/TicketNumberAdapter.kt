package com.newitzone.tambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.tambola.R
import kotlinx.android.synthetic.main.cardview_open_random_number.view.*
import kotlinx.android.synthetic.main.cardview_ticket_number.view.*
import kotlinx.android.synthetic.main.cardview_tournament_item.view.*
import model.NumModel

class TicketNumberAdapter(val items : List<NumModel>, val context: Context) : RecyclerView.Adapter<ViewHolderTicketNum>() {

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
        // set text
        if (items[position].num == 0){
            holder?.tvNum?.text = ""
        }else{
            holder?.tvNum?.text = items[position].num.toString()
        }
        // set background
        if (items[position].isChecked){
            holder?.tvNum.setBackgroundResource(R.drawable.select_patch)
        }else{
            holder?.tvNum.setBackgroundResource(android.R.color.transparent)
        }
        // onClick
        holder.rL.tag = items[position]
        holder.rL.setOnClickListener {
            val numModel = it.tag as NumModel
            if (numModel != null && numModel.num != 0){
                items[position].isChecked = !numModel.isChecked
                notifyDataSetChanged()
            }
        }
    }
}

class ViewHolderTicketNum (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvNum: TextView = view.text_num
    val rL: RelativeLayout = view.relative_num
}