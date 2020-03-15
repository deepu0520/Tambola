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
import com.newitzone.tambola.HomeActivity
import com.newitzone.tambola.PlayActivity
import com.newitzone.tambola.R
import com.newitzone.tambola.adapter.PriceAdapter
import com.newitzone.tambola.adapter.TournamentAdapter
import com.newitzone.tambola.utils.RecyclerItemClickListenr
import model.KeyModel

class TournamentGamesDialog : DialogFragment() {
    private lateinit var keyModel: KeyModel
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
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_tournament_games, parent, false)
        if (arguments != null) {
            val mArgs = arguments
            keyModel= mArgs!!.getSerializable(HomeActivity.KEY_MODEL) as KeyModel
        }
        ButterKnife.bind(this, view)
        val context: Context = requireContext()
        val recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        // Initializing an empty ArrayList to be filled with items
        val menuList: List<String> = resources.getStringArray(R.array.price_list).asList()
        val adapter = TournamentAdapter(menuList,context)
        recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerView.adapter = adapter
        recyclerView.addOnItemTouchListener(RecyclerItemClickListenr(context, recyclerView, object : RecyclerItemClickListenr.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                //TODO: Use this
                val intent = Intent(activity, PlayActivity::class.java)
                intent.putExtra(HomeActivity.KEY_MODEL,keyModel)
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                activity!!.startActivity(intent)
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