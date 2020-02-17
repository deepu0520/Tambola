package com.newitzone.tambola

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.newitzone.tambola.adapter.LiveUserAdapter
import com.newitzone.tambola.adapter.RandomNumberAdapter
import com.newitzone.tambola.adapter.TicketNumberAdapter
import com.newitzone.tambola.dialog.TicketsDialog
import com.newitzone.tambola.utils.RecyclerItemClickListenr
import org.jetbrains.annotations.TestOnly
import java.util.Random

class PlayActivity : AppCompatActivity() {
    private var context: Context? = null
    val random = Random()
    @BindView(R.id.linear_ticket_1) lateinit var lLTicket1: LinearLayout
    @BindView(R.id.linear_ticket_2) lateinit var lLTicket2: LinearLayout
    @BindView(R.id.recycler_view_ticket_1) lateinit var rVTicket1: RecyclerView
    @BindView(R.id.recycler_view_ticket_2) lateinit var rVTicket2: RecyclerView
    @BindView(R.id.text_btn_ticket_claim_1) lateinit var tvBtnClaimTicket1: TextView
    @BindView(R.id.text_btn_ticket_claim_2) lateinit var tvBtnClaimTicket2: TextView
    @BindView(R.id.recycler_view_claim_ticket_1) lateinit var rVClaimTicket1: RecyclerView
    @BindView(R.id.recycler_view_claim_ticket_2) lateinit var rVClaimTicket2: RecyclerView

    @BindView(R.id.recycler_view_random_number) lateinit var recyclerViewRanNum: RecyclerView
    @BindView(R.id.recycler_view_live_user) lateinit var recyclerViewLiveUser: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_play)
        supportActionBar?.hide()
        this.context = this@PlayActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        val ticket:Int = intent.getIntExtra(TicketsDialog.KEY_TICKET,0)
        if (ticket == 1){
            lLTicket1.visibility = View.VISIBLE
            lLTicket2.visibility = View.GONE
        }else if (ticket == 2){
            lLTicket1.visibility = View.VISIBLE
            lLTicket2.visibility = View.VISIBLE
        }
        // TODO: on Click test
        // TODO: Random Number open
        onRecyclerViewRandomNumber()
        // TODO: Live User
        onRecyclerViewLiveUser()
        // TODO: Ticket 1
        onTicket1()
        // TODO: Ticket 2
        onTicket2()
    }
    fun onRecyclerViewRandomNumber(){
        // Initializing an empty ArrayList to be filled with items
        val menuList: List<String> = resources.getStringArray(R.array.random_number_list).asList()
        val adapter = RandomNumberAdapter(menuList,this)
        recyclerViewRanNum.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerViewRanNum.adapter = adapter
        recyclerViewRanNum.addOnItemTouchListener(RecyclerItemClickListenr(this, recyclerViewRanNum, object : RecyclerItemClickListenr.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                //TODO: Use this
            }
            override fun onItemLongClick(view: View?, position: Int) {
                TODO("do nothing")
            }
        }))
    }
    fun onRecyclerViewLiveUser(){
        // Initializing an empty ArrayList to be filled with items
        val list: List<String> = resources.getStringArray(R.array.live_user_list).asList()
        val adapter = LiveUserAdapter(list,this)
        recyclerViewLiveUser.layoutManager = LinearLayoutManager(context)
        recyclerViewLiveUser.adapter = adapter
        recyclerViewLiveUser.addOnItemTouchListener(RecyclerItemClickListenr(this, recyclerViewLiveUser, object : RecyclerItemClickListenr.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                //TODO: Use this
            }
            override fun onItemLongClick(view: View?, position: Int) {
                TODO("do nothing")
            }
        }))
    }
    fun onTicket1(){
        val numList: MutableList<Int> = mutableListOf()
        for (i in 1..27){
            numList.add(rand(1,90))
        }
        val adapter = TicketNumberAdapter(numList,this)
        rVTicket1.layoutManager = GridLayoutManager(context,9)
        rVTicket1.adapter = adapter
    }
    fun onTicket2(){
        val numList: MutableList<Int> = mutableListOf()
        for (i in 1..27){
            numList.add(rand(1,90))
        }
        val adapter = TicketNumberAdapter(numList,this)
        rVTicket2.layoutManager = GridLayoutManager(context,9)
        rVTicket2.adapter = adapter
    }


    fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }
    override fun onBackPressed() { // do something here and don't write super.onBackPressed()
        super.onBackPressed()
        finish()
    }
}
