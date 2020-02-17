package com.newitzone.tambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.tambola.R
import kotlinx.android.synthetic.main.cardview_claim_ticket.view.*
import kotlinx.android.synthetic.main.cardview_live_user.view.*
import kotlinx.android.synthetic.main.cardview_open_random_number.view.*
import kotlinx.android.synthetic.main.cardview_tournament_item.view.*

class ClaimTicketAdapter(val items : List<String>, val context: Context) : RecyclerView.Adapter<ViewHolderClaimTicket>() {

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
        holder?.tvPrizeName?.text = items.get(position)
    }
}

class ViewHolderClaimTicket (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvPrizeName = view.text_prize_name
}