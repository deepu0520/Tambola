package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.databinding.CardviewTransItemBinding
import com.newitzone.desitambola.utils.UtilMethods
import model.transaction.Result


class CashOrChipsTransAdapter(val items: List<Result>, val context: Context) :
    RecyclerView.Adapter<ViewHolderCashOrChips>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCashOrChips {
        val binding =
            CardviewTransItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderCashOrChips(binding)
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderCashOrChips, position: Int) {
//        val getRow: Any = items[position]
//        val t: LinkedTreeMap<*, *> = getRow as LinkedTreeMap<*, *>
        holder.tvOrderNoHeader.text = "Order No"
        holder?.tvOrderNo?.text = items[position].docNo //t["doc_no"] as CharSequence?

        holder?.tvTransDateHeader?.text = "Trans Datetime"
        holder?.tvTransDate?.text =
            UtilMethods.getDatetimeDDMMYYYY_HHMMSS(items[position].transDt)//""+t["trans_dt"] as CharSequence?)

        holder?.tvAmtHeader?.text = "Amount"
        holder?.tvAmt?.text =
            "₹" + UtilMethods.roundOffDecimal(items[position].amt.toDouble()) + "/-"

        holder?.tvStatusHeader?.text = "Status"
        holder?.tvStatus?.text = items[position].transType//t["TransType"] as CharSequence?

        holder?.tvRemarksHeader?.text = "Description"
        holder?.tvRemarks?.text = items[position].descrption//t["descrption"] as CharSequence?
    }
}

class ViewHolderCashOrChips(val view: CardviewTransItemBinding) :
    RecyclerView.ViewHolder(view.root) {
    // Holds the TextView that will add each animal to
    val tvOrderNoHeader = view.textOrderNoHeader
    val tvTransDateHeader = view.textTransDateTimeHeader
    val tvAmtHeader = view.textOrderAmountHeader
    val tvStatusHeader = view.textStatusHeader
    val tvRemarksHeader = view.textRemarksHeader

    //
    val tvOrderNo = view.textOrderNo
    val tvTransDate = view.textTransDateTime
    val tvAmt = view.textOrderAmount
    val tvStatus = view.textStatus
    val tvRemarks = view.textRemarks
}