package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.PlayActivity
import com.newitzone.desitambola.R
import kotlinx.android.synthetic.main.cardview_result_item.view.*
import model.result.Result

class ResultAdapter(val items : List<Result>, val context: Context) : RecyclerView.Adapter<ViewHolderResult>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderResult {
        return ViewHolderResult(LayoutInflater.from(context).inflate(R.layout.cardview_result_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderResult, position: Int) {
        holder?.tvPrize?.text =  items[position].prize
        holder?.tvName?.text = items[position].userName

        when (items[position].gameType) {
            PlayActivity.SAMPLE_GAME -> {
                holder?.tvAmt?.text = items[position].amount.toString()+"(Chips)"
            }
            PlayActivity.CASH_GAME -> {
                holder?.tvAmt?.text = items[position].amount.toString()+"(₹)"
            }
            PlayActivity.TOURNAMENT_GAME -> {
                holder?.tvAmt?.text = items[position].amount.toString()+"(₹)"
            }
        }
    }
}

class ViewHolderResult (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvPrize = view.text_prize
    val tvName = view.text_name
    val tvAmt = view.text_amt
}