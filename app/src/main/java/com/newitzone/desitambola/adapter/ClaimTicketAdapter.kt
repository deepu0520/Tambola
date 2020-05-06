package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.R
import kotlinx.android.synthetic.main.cardview_claim_ticket.view.*
import model.claim.PrizeModel

class ClaimTicketAdapter(val items : List<PrizeModel>, val context: Context) : RecyclerView.Adapter<ViewHolderClaimTicket>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderClaimTicket {
        return ViewHolderClaimTicket(LayoutInflater.from(context).inflate(R.layout.cardview_claim_ticket, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderClaimTicket, position: Int) {
        holder?.tvPrizeName?.text = items[position].name
        if (items[position].isChecked){
            holder?.lLItem.setBackgroundResource(R.drawable.round_corner_disable)
            holder?.tvPrizeName?.text = items[position].name
            holder?.tvClaimUserName?.text = "(By "+items[position].userName + ")"
            holder?.tvClaimUserName?.visibility = View.VISIBLE
        }else{
            holder?.lLItem.setBackgroundResource(R.drawable.round_corner_enable)
            holder?.tvPrizeName?.text = items[position].name
            holder?.tvClaimUserName?.visibility = View.GONE
        }
    }
}

class ViewHolderClaimTicket (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvPrizeName: TextView = view.text_prize_name
    val tvClaimUserName: TextView = view.text_prize_claim_username
    val lLItem: LinearLayout = view.linear_item
}