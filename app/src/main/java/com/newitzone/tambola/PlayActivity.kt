package com.newitzone.tambola

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.newitzone.tambola.adapter.ClaimTicketAdapter
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
    var claimTicket1 = true
    var claimTicket2 = true
    var sCash = 0
    var sTournament = 0
    private var i = 0
    private val handler = Handler()
    var randomNumList: MutableList<String> = mutableListOf<String>()

    @BindView(R.id.progress_bar) lateinit var progressBar: ProgressBar
    @BindView(R.id.text_per) lateinit var tvPer: TextView
    @BindView(R.id.text_ran_num) lateinit var tvRanNum: TextView
    @BindView(R.id.relative_pro_bar) lateinit var rLProBar: RelativeLayout

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
        sCash = intent.getIntExtra(HomeActivity.KEY_CASH,0)
        sTournament = intent.getIntExtra(HomeActivity.KEY_TOURNAMENT,0)
        val ticket:Int = intent.getIntExtra(TicketsDialog.KEY_TICKET,0)
        if (ticket == 1){
            lLTicket1.visibility = View.VISIBLE
            lLTicket2.visibility = View.GONE
        }else if (ticket == 2){
            lLTicket1.visibility = View.VISIBLE
            lLTicket2.visibility = View.VISIBLE
        }
//        progressBar.setOnClickListener { view ->
//            onRecyclerViewRandomNumber()
//        }
        // TODO: Profle update
        onProfileUpdate()
        // TODO: Random Number open
        onRecyclerViewRandomNumber()
        // TODO: Live User
        onRecyclerViewLiveUser()
        // TODO: Ticket 1
        //onTicket1()
        // TODO: Ticket 2
        //onTicket2()
        // TODO: Recycler view Claim Ticket 1
        onRecyclerViewClaimTicket1()
        // TODO: Recycler view Claim Ticket 2
        onRecyclerViewClaimTicket2()
        // TODO: Claim Ticket 1
        tvBtnClaimTicket1.setOnClickListener { view ->
            onClaimTicket1(view)
        }
        // TODO: Claim Ticket 2
        tvBtnClaimTicket2.setOnClickListener { view ->
            onClaimTicket2(view)
        }
    }
    fun onProfileUpdate(){
        val intent = Intent(context, ProfileActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }
    fun onRecyclerViewRandomNumber(){
        var ranNum = rand(1,90)
        progressBar.progress = 1
        i = progressBar.progress
        Thread(Runnable {
            while (i < 100) {
                i += 1
                // Update the progress bar and display the current value
                handler.post(Runnable {
                    progressBar.progress = i
                    tvPer!!.text = i.toString()// + "%/" + progressBar!!.max
                    tvRanNum!!.text = ""+ranNum
                })
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            handler.post {
                randomNumList.add(ranNum.toString())
                // Initializing an empty ArrayList to be filled with items
                val adapter = RandomNumberAdapter(randomNumList,this)
                recyclerViewRanNum.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                recyclerViewRanNum.adapter = adapter
                recyclerViewRanNum.scrollToPosition(randomNumList.size - 1)

                if(randomNumList.size < 90) {
                    // restart
                    onRecyclerViewRandomNumber()
                }else{
                    rLProBar.visibility = View.INVISIBLE
                }
            }
        }).start()
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
    fun onClaimTicket1(view: View) {
        if (claimTicket1){
            rVClaimTicket1.visibility = View.VISIBLE
            claimTicket1 = false
            tvBtnClaimTicket1.text = resources.getString(R.string.txt_ticket_hide_1)
        }else{
            rVClaimTicket1.visibility = View.GONE
            claimTicket1 = true
            tvBtnClaimTicket1.text = resources.getString(R.string.txt_ticket_1)
        }
    }
    fun onClaimTicket2(view: View) {
        if (claimTicket2){
            rVClaimTicket2.visibility = View.VISIBLE
            claimTicket2 = false
            tvBtnClaimTicket2.text = resources.getString(R.string.txt_ticket_hide_2)
        }else{
            rVClaimTicket2.visibility = View.GONE
            claimTicket2 = true
            tvBtnClaimTicket2.text = resources.getString(R.string.txt_ticket_2)
        }
    }
    fun onRecyclerViewClaimTicket1(){
        // Initializing an empty ArrayList to be filled with items
        var prizeList: List<String> = emptyList()
        if (sCash == 1){
            prizeList = resources.getStringArray(R.array.cash_claim_ticket_list).asList()
            rVClaimTicket1.layoutManager = GridLayoutManager(context,2)
        }else if (sTournament == 1){
            prizeList = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()
            rVClaimTicket1.layoutManager = GridLayoutManager(context,3)
        }else {
            prizeList = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()
            rVClaimTicket1.layoutManager = GridLayoutManager(context,3)
        }
        val adapter = ClaimTicketAdapter(prizeList,this)
        rVClaimTicket1.adapter = adapter
    }
    fun onRecyclerViewClaimTicket2(){
        // Initializing an empty ArrayList to be filled with items
        var prizeList: List<String> = emptyList()
        if (sCash == 1){
            prizeList = resources.getStringArray(R.array.cash_claim_ticket_list).asList()
            rVClaimTicket2.layoutManager = GridLayoutManager(context,2)
        }else if (sTournament == 1){
            prizeList = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()
            rVClaimTicket2.layoutManager = GridLayoutManager(context,3)
        }else {
            prizeList = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()
            rVClaimTicket2.layoutManager = GridLayoutManager(context,3)
        }
        val adapter = ClaimTicketAdapter(prizeList,this)
        rVClaimTicket2.adapter = adapter
    }
    fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }
    override fun onBackPressed() { // do something here and don't write super.onBackPressed()
        super.onBackPressed()
        finish()
    }
}
