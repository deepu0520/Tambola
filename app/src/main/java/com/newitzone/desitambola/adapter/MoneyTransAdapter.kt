package com.newitzone.desitambola.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.internal.LinkedTreeMap
import com.newitzone.desitambola.R
import com.newitzone.desitambola.utils.UtilMethods
import kotlinx.android.synthetic.main.cardview_trans_item.view.*
import model.money.transaction.Result

class MoneyTransAdapter(val items : List<Result>, val context: Context) : RecyclerView.Adapter<ViewHolderMoney>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMoney {
        return ViewHolderMoney(LayoutInflater.from(context).inflate(R.layout.cardview_trans_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderMoney, position: Int) {
        val getRow: Any = items[position]
        val t: LinkedTreeMap<*, *> = getRow as LinkedTreeMap<*, *>

        holder?.tvOrderNoHeader?.text = "Order No"
        holder?.tvOrderNo?.text = t["doc_no"] as CharSequence?

        holder?.tvTransDateHeader?.text = "Trans Datetime"
        holder?.tvTransDate?.text = UtilMethods.getDatetimeDDMMYYYY_HHMMSS(""+t["entry_datetime"] as CharSequence?)

        holder?.tvAmtHeader?.text = "Amount"
        holder?.tvAmt?.text = "₹"+t["amt"] as CharSequence?+"/-"

        holder?.tvStatusHeader?.text = "Status"
        holder?.tvStatus?.text = t["status"] as CharSequence?

        holder?.tvRemarksHeader?.text = "Remarks"
        holder?.tvRemarks?.text = t["payment_getway_resp"] as CharSequence?
    }
}

class ViewHolderMoney (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvOrderNoHeader: TextView = view.text_order_no_header
    val tvTransDateHeader: TextView = view.text_trans_date_time_header
    val tvAmtHeader: TextView = view.text_order_amount_header
    val tvStatusHeader: TextView = view.text_status_header
    val tvRemarksHeader: TextView = view.text_remarks_header
    //
    val tvOrderNo: TextView = view.text_order_no
    val tvTransDate: TextView = view.text_trans_date_time
    val tvAmt: TextView = view.text_order_amount
    val tvStatus: TextView = view.text_status
    val tvRemarks: TextView = view.text_remarks
}