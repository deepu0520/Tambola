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
import model.transaction.Result


class CashOrChipsTransAdapter(val items : List<Result>, val context: Context) : RecyclerView.Adapter<ViewHolderCashOrChips>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCashOrChips {
        return ViewHolderCashOrChips(LayoutInflater.from(context).inflate(R.layout.cardview_trans_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolderCashOrChips, position: Int) {
//        val getRow: Any = items[position]
//        val t: LinkedTreeMap<*, *> = getRow as LinkedTreeMap<*, *>
        holder?.tvOrderNoHeader?.text = "Order No"
        holder?.tvOrderNo?.text = items[position].docNo //t["doc_no"] as CharSequence?

        holder?.tvTransDateHeader?.text = "Trans Datetime"
        holder?.tvTransDate?.text = UtilMethods.getDatetimeDDMMYYYY_HHMMSS(items[position].transDt)//""+t["trans_dt"] as CharSequence?)

        holder?.tvAmtHeader?.text = "Amount"
        holder?.tvAmt?.text = "₹"+UtilMethods.roundOffDecimal(items[position].amt.toDouble())+"/-"

        holder?.tvStatusHeader?.text = "Status"
        holder?.tvStatus?.text = items[position].transType//t["TransType"] as CharSequence?

        holder?.tvRemarksHeader?.text = "Description"
        holder?.tvRemarks?.text = items[position].descrption//t["descrption"] as CharSequence?
    }
}

class ViewHolderCashOrChips (view: View) : RecyclerView.ViewHolder(view) {
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