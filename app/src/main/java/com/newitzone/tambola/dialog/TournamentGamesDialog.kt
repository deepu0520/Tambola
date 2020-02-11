package com.newitzone.tambola.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newitzone.tambola.PlayActivity
import com.newitzone.tambola.R

class TournamentGamesDialog : DialogFragment() {

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
            activity!!.layoutInflater.inflate(R.layout.dialog_tournament_games, parent, false)
        ButterKnife.bind(this, view)
        return view
    }

    @OnClick(R.id.linear_club)
    fun onClub(view: View?) {
        val dialog = TicketsDialog()
        val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        dialog.show(ft, TicketsDialog.TAG)
    }

    @OnClick(R.id.linear_special)
    fun onSpecial(view: View?) {
        val dialog = TicketsDialog()
        val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        dialog.show(ft, TicketsDialog.TAG)
    }
    @OnClick(R.id.linear_jackpot)
    fun onJackpot(view: View?) {
        val dialog = TicketsDialog()
        val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        dialog.show(ft, TicketsDialog.TAG)
    }
    @OnClick(R.id.linear_sunday)
    fun onSunday(view: View?) {
        val dialog = TicketsDialog()
        val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        dialog.show(ft, TicketsDialog.TAG)
    }
    @OnClick(R.id.linear_cash)
    fun onCash(view: View?) {
        val dialog = TicketsDialog()
        val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        dialog.show(ft, TicketsDialog.TAG)
    }
    @OnClick(R.id.linear_saturday)
    fun onSaturday(view: View?) {
        val dialog = TicketsDialog()
        val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        dialog.show(ft, TicketsDialog.TAG)
    }
    @OnClick(R.id.linear_weekend)
    fun onWeekend(view: View?) {
        val dialog = TicketsDialog()
        val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
        dialog.show(ft, TicketsDialog.TAG)
    }

    companion object {
        const val TAG = "FullScreenDialog"
        const val KEY_TICKET = "Key_Ticket"
    }
}