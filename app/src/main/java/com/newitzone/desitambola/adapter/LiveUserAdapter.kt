package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.R
import com.newitzone.desitambola.databinding.CardviewLiveUserBinding
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import model.gamein.UserData

class LiveUserAdapter(val items: List<UserData>, val context: Context) :
    RecyclerView.Adapter<ViewHolderLiveUser>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderLiveUser {
        val binding =
            CardviewLiveUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderLiveUser(binding)
        // return ViewHolderLiveUser(LayoutInflater.from(context).inflate(R.layout.cardview_live_user, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderLiveUser, position: Int) {
        holder?.tvUserName?.text = items[position].name
        when (position) {
            0 -> {
                holder?.imgUser?.setImageResource(R.drawable.avtar0)
            }

            1 -> {
                holder?.imgUser?.setImageResource(R.drawable.avtar1)
            }

            2 -> {
                holder?.imgUser?.setImageResource(R.drawable.avtar2)
            }

            3 -> {
                holder?.imgUser?.setImageResource(R.drawable.avtar3)
            }

            4 -> {
                holder?.imgUser?.setImageResource(R.drawable.avtar4)
            }

            5 -> {
                holder?.imgUser?.setImageResource(R.drawable.avtar5)
            }
        }
        if (items[position].img != null && items[position].img.isNotBlank()) {
            // load the image with Picasso
            Picasso.get().load(items[position].img).memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(holder?.imgUser) // select the ImageView to load it into

        } else if (items[position].fbImg != null && items[position].fbImg.isNotBlank()) {
            // load the image with Picasso
            Picasso.get().load(items[position].fbImg).memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(holder?.imgUser) // select the ImageView to load it into
        }
    }
}

class ViewHolderLiveUser(view: CardviewLiveUserBinding) : RecyclerView.ViewHolder(view.root) {
    // Holds the TextView that will add each animal to
    val tvUserName = view.textUserName
    val imgUser: CircleImageView = view.imageLiveUser
}