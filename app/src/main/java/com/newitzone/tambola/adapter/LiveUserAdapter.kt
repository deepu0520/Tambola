package com.newitzone.tambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.tambola.R
import kotlinx.android.synthetic.main.cardview_live_user.view.*
import kotlinx.android.synthetic.main.cardview_open_random_number.view.*
import kotlinx.android.synthetic.main.cardview_tournament_item.view.*
import model.gamein.Result
import model.gameinv2.UserData

class LiveUserAdapter(val items : List<UserData>, val context: Context) : RecyclerView.Adapter<ViewHolderLiveUser>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderLiveUser {
        return ViewHolderLiveUser(LayoutInflater.from(context).inflate(R.layout.cardview_live_user, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderLiveUser, position: Int) {
        holder?.tvUserName?.text = items[position].name
    }
}

class ViewHolderLiveUser (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvUserName: TextView = view.text_user_name
}