package com.newitzone.tambola.dialog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.mod.rdpindia.ui.utils.RecyclerItemClickListenr
import com.newitzone.tambola.PlayActivity
import com.newitzone.tambola.R
import com.newitzone.tambola.adapter.PriceAdapter
import kotlinx.android.synthetic.main.dialog_cash_games.*

class CashGamesDialog : DialogFragment() {

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
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_cash_games, parent, false)
        val context:Context = requireContext()
        ButterKnife.bind(this, view)
        // Initializing an empty ArrayList to be filled with items
        val menuList: List<String> = resources.getStringArray(R.array.price_list).asList()
        val adapter = PriceAdapter(menuList,context)
        var recycler_view = view.findViewById(R.id.recycler_view) as RecyclerView
        recycler_view.layoutManager = GridLayoutManager(context,3)
        recycler_view.adapter = adapter
        recycler_view.addOnItemTouchListener(RecyclerItemClickListenr(context, recycler_view, object : RecyclerItemClickListenr.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                //TODO: Use this
                val dialog = TicketsDialog()
                val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
                dialog.show(ft, TicketsDialog.TAG)
            }
            override fun onItemLongClick(view: View?, position: Int) {
                TODO("do nothing")
            }
        }))
        return view
    }

    companion object {
        const val TAG = "FullScreenDialog"
        const val KEY_TICKET = "Key_Ticket"
    }
}