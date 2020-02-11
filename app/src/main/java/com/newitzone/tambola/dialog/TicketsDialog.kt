package com.newitzone.tambola.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newitzone.tambola.PlayActivity
import com.newitzone.tambola.R

class TicketsDialog : DialogFragment() {
    @JvmField
    @BindView(R.id.image_ticket_1)
    var imgTicket1: ImageView? = null
    @JvmField
    @BindView(R.id.image_ticket_2)
    var imgTicket2: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        state: Bundle?
    ): View? {
        super.onCreateView(inflater, parent, state)
        val view =
            activity!!.layoutInflater.inflate(R.layout.dialog_tickets, parent, false)
        ButterKnife.bind(this, view)
        return view
    }

    @OnClick(R.id.image_ticket_1)
    fun onTicket1(view: View?) {
        val intent = Intent(activity, PlayActivity::class.java)
        intent.putExtra(KEY_TICKET,1)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        activity!!.startActivity(intent)
    }

    @OnClick(R.id.image_ticket_2)
    fun onTicket2(view: View?) {
        val intent = Intent(activity, PlayActivity::class.java)
        intent.putExtra(KEY_TICKET,2)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        activity!!.startActivity(intent)
    }

    companion object {
        const val TAG = "FullScreenDialog"
        const val KEY_TICKET = "Key_Ticket"
    }
}