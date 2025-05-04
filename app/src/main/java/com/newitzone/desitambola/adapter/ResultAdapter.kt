package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.PlayActivity
import com.newitzone.desitambola.databinding.CardviewResultItemBinding
import model.result.Result

class ResultAdapter(val items: List<Result>, val context: Context) :
    RecyclerView.Adapter<ViewHolderResult>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderResult {
        val binding =
            CardviewResultItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolderResult(binding)

        // return ViewHolderResult(LayoutInflater.from(context).inflate(R.layout.cardview_result_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderResult, position: Int) {
        holder?.tvPrize?.text = items[position].prize
        holder?.tvName?.text = items[position].userName

        when (items[position].gameType) {
            PlayActivity.SAMPLE_GAME -> {
                holder?.tvAmt?.text = items[position].amount.toString() + "(Chips)"
            }

            PlayActivity.CASH_GAME -> {
                holder?.tvAmt?.text = items[position].amount.toString() + "(₹)"
            }

            PlayActivity.TOURNAMENT_GAME -> {
                holder?.tvAmt?.text = items[position].amount.toString() + "(₹)"
            }
        }
    }
}

class ViewHolderResult(view: CardviewResultItemBinding) : RecyclerView.ViewHolder(view.root) {
    // Holds the TextView that will add each animal to
    val tvPrize = view.textPrize
    val tvName = view.textName
    val tvAmt = view.textAmt
}