package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.R
import com.newitzone.desitambola.databinding.CardviewTicketNumberBinding
import model.NumModel

class TicketNumberAdapter(val items: List<NumModel>, val context: Context) :
    RecyclerView.Adapter<ViewHolderTicketNum>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTicketNum {
        val binding =
            CardviewTicketNumberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolderTicketNum(binding)
        // return ViewHolderTicketNum(LayoutInflater.from(context).inflate(R.layout.cardview_ticket_number, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderTicketNum, position: Int) {
        // set text
        if (items[position].num == 0) {
            holder?.tvNum?.text = ""
//            holder?.rL.setBackgroundResource(android.R.color.transparent)
        } else {
            holder?.tvNum?.text = items[position].num.toString()
//            holder?.rL.setBackgroundResource(R.color.color_yellow)
        }
        // set background
        if (items[position].isChecked) {
            holder?.tvNum?.setBackgroundResource(R.drawable.select_patch)
        } else {
            holder?.tvNum?.setBackgroundResource(android.R.color.transparent)
        }
        // onClick
        holder.rL.tag = items[position]
        holder.rL.setOnClickListener {
            val numModel = it.tag as NumModel
            if (numModel != null && numModel.num != 0) {
                if (!items[position].isTick) {
                    items[position].isChecked = !numModel.isChecked
                    notifyDataSetChanged()
                }
            }
        }
    }
}

class ViewHolderTicketNum(view: CardviewTicketNumberBinding) : RecyclerView.ViewHolder(view.root) {
    // Holds the TextView that will add each animal to
    val tvNum = view.textNum
    val rL = view.relativeNum
}