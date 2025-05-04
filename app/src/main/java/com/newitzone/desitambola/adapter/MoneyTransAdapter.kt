package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.databinding.CardviewTransItemBinding
import com.newitzone.desitambola.utils.UtilMethods
import model.money.transaction.Result

class MoneyTransAdapter(val items: List<Result>, val context: Context) :
    RecyclerView.Adapter<ViewHolderMoney>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMoney {
        val binding =
            CardviewTransItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderMoney(binding)
        /* return ViewHolderMoney(
             LayoutInflater.from(context).inflate(R.layout.cardview_trans_item, parent, false)
         )*/
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderMoney, position: Int) {
//        val getRow: Any = items[position]
//        val t: LinkedTreeMap<*, *> = getRow as LinkedTreeMap<*, *>

        holder?.tvOrderNoHeader?.text = "Order No"
        holder?.tvOrderNo?.text = items[position].docNo//t["doc_no"] as CharSequence?

        holder?.tvTransDateHeader?.text = "Trans Datetime"
        holder?.tvTransDate?.text =
            UtilMethods.getDatetimeDDMMYYYY_HHMMSS(items[position].entryDatetime)//""+t["entry_datetime"] as CharSequence?)

        holder?.tvAmtHeader?.text = "Amount"
        holder?.tvAmt?.text =
            "₹" + UtilMethods.roundOffDecimal(items[position].amt.toDouble()) + "/-"

        holder?.tvStatusHeader?.text = "Status"
        holder?.tvStatus?.text = items[position].status//t["status"] as CharSequence?

        holder?.tvRemarksHeader?.text = "Remarks"
        holder?.tvRemarks?.text = ""//t["payment_getway_resp"] as CharSequence?
    }
}

class ViewHolderMoney(view: CardviewTransItemBinding) : RecyclerView.ViewHolder(view.root) {
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