package com.newitzone.tambola

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.newitzone.tambola.adapter.*
import com.newitzone.tambola.utils.RecyclerItemClickListenr
import com.newitzone.tambola.utils.SharedPrefManager
import model.KeyModel
import model.NumModel
import model.gamein.GameIn
import model.gamein.Result
import java.util.*

class PlayActivity : AppCompatActivity() {
    private var context: Context? = null
    private lateinit var keyModel: KeyModel
    private lateinit var keyGameInResponse: GameIn
    private lateinit var keyGameInResult: Result
    private val random = Random()
    var claimTicket1 = true
    var claimTicket2 = true
    private var i = 0
    private var posRanNum = 0
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
    @BindView(R.id.recycler_view_number_grid) lateinit var recyclerViewNoGrid: RecyclerView
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
        keyModel = intent.getSerializableExtra(HomeActivity.KEY_MODEL) as KeyModel
        keyGameInResponse = intent.getSerializableExtra(HomeActivity.KEY_GAME_IN) as GameIn
        if (keyModel != null) {
            if (keyModel.ticketType == 1) {
                lLTicket1.visibility = View.VISIBLE
                lLTicket2.visibility = View.GONE
            } else if (keyModel.ticketType == 2) {
                lLTicket1.visibility = View.VISIBLE
                lLTicket2.visibility = View.VISIBLE
            }
        }
        if (keyGameInResponse != null){
            for (elements in keyGameInResponse.result) {
                if (elements.userId == SharedPrefManager.getInstance(context as PlayActivity).result.id) {
                    keyGameInResult = elements
                    break
                }
            }
        }
//        progressBar.setOnClickListener { view ->
//            onRecyclerViewRandomNumber()
//        }
        // TODO: Random Number open
        onRecyclerViewRandomNumber()
        // TODO: Live User
        onRecyclerViewLiveUser()
        // TODO: Number Grid
        //onRecyclerViewNumberGrid()
        // TODO: Ticket 1
        onTicket1()
        // TODO: Ticket 2
        onTicket2()
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
    private fun onRecyclerViewRandomNumber(){
        //var ranNum = rand(1,90)
        if (keyGameInResult != null) {
            var ranNum = keyGameInResult.randNo[posRanNum]
            progressBar.progress = 1
            i = progressBar.progress
            Thread(Runnable {
                while (i < 100) {
                    i += 1
                    // Update the progress bar and display the current value
                    handler.post(Runnable {
                        progressBar.progress = i
                        tvPer!!.text = i.toString()// + "%/" + progressBar!!.max
                        tvRanNum!!.text = "" + ranNum
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
                    val adapter = RandomNumberAdapter(randomNumList, this)
                    recyclerViewRanNum.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    recyclerViewRanNum.adapter = adapter
                    recyclerViewRanNum.scrollToPosition(randomNumList.size - 1)

                    if (randomNumList.size < 90) {
                        posRanNum++
                        // restart
                        onRecyclerViewRandomNumber()
                    } else {
                        rLProBar.visibility = View.INVISIBLE
                    }
                }
            }).start()
        }
    }
    private fun onRecyclerViewLiveUser(){
        if (keyGameInResponse != null) {
            // Initializing an empty ArrayList to be filled with items
            val list: List<Result> = keyGameInResponse.result
            val adapter = LiveUserAdapter(list, this)
            recyclerViewLiveUser.layoutManager = LinearLayoutManager(context)
            recyclerViewLiveUser.adapter = adapter
            recyclerViewLiveUser.addOnItemTouchListener(RecyclerItemClickListenr(this, recyclerViewLiveUser,
                    object : RecyclerItemClickListenr.OnItemClickListener {

                        override fun onItemClick(view: View, position: Int) {
                            //TODO: Use this
                        }

                        override fun onItemLongClick(view: View?, position: Int) {
                            TODO("do nothing")
                        }
                    })
            )
        }
    }
    private fun onTicket1(){
        if (keyGameInResult != null) {
            var i = 0
            val numList: MutableList<NumModel> = mutableListOf()
            val listRow1 = keyGameInResult.tick.ticket1[0] as List<Int>
            for (element in listRow1) {
                val numModel = NumModel(element,false)
                numList.add(i,numModel)
                i++
            }
            val listRow2 = keyGameInResult.tick.ticket1[1] as List<Int>
            for (element in listRow2) {
                val numModel = NumModel(element,false)
                numList.add(i,numModel)
                i++
            }
            val listRow3 = keyGameInResult.tick.ticket1[2] as List<Int>
            for (element in listRow3) {
                val numModel = NumModel(element,false)
                numList.add(i,numModel)
                i++
            }
//            print(numList)
//            for (i in 1..27) {
//                numList.add(rand(1, 90))
//            }
            val adapter = TicketNumberAdapter(numList, this)
            rVTicket1.layoutManager = GridLayoutManager(context, 9)
            rVTicket1.adapter = adapter
//            rVTicket1.addOnItemTouchListener(RecyclerItemClickListenr(context, rVTicket1, object : RecyclerItemClickListenr.OnItemClickListener {
//
//                override fun onItemClick(view: View, position: Int) {
//
//                }
//                override fun onItemLongClick(view: View?, position: Int) {
//                    TODO("do nothing")
//                }
//            }))
        }
    }
    private fun onTicket2(){
        if (keyGameInResult != null && keyModel.ticketType == 2) {
            var i = 0
            val numList: MutableList<NumModel> = mutableListOf()
            val listRow1 = keyGameInResult.tick.ticket2[0] as List<Int>
            for (element in listRow1) {
                val numModel = NumModel(element,false)
                numList.add(i,numModel)
                i++
            }
            val listRow2 = keyGameInResult.tick.ticket2[1] as List<Int>
            for (element in listRow2) {
                val numModel = NumModel(element,false)
                numList.add(i,numModel)
                i++
            }
            val listRow3 = keyGameInResult.tick.ticket2[2] as List<Int>
            for (element in listRow3) {
                val numModel = NumModel(element,false)
                numList.add(i,numModel)
                i++
            }
//        for (i in 1..27){
//            numList.add(rand(1,90))
//        }
            val adapter = TicketNumberAdapter(numList, this)
            rVTicket2.layoutManager = GridLayoutManager(context, 9)
            rVTicket2.adapter = adapter
        }
    }
    private fun onClaimTicket1(view: View) {
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
    private fun onClaimTicket2(view: View) {
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
    private fun onRecyclerViewClaimTicket1(){
        // Initializing an empty ArrayList to be filled with items
        var prizeList: List<String> = emptyList()
        if (keyModel.gameType == 1){
            // todo: game type: cash
            prizeList = resources.getStringArray(R.array.cash_claim_ticket_list).asList()
            rVClaimTicket1.layoutManager = GridLayoutManager(context,2)
        }else if (keyModel.gameType == 2){
            // todo: game type: tournament
            prizeList = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()
            rVClaimTicket1.layoutManager = GridLayoutManager(context,3)
        }else {
            // todo: game type: sample
            prizeList = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()
            rVClaimTicket1.layoutManager = GridLayoutManager(context,3)
        }
        val adapter = ClaimTicketAdapter(prizeList,this)
        rVClaimTicket1.adapter = adapter
        rVClaimTicket1.addOnItemTouchListener(RecyclerItemClickListenr(context as PlayActivity, rVTicket1, object : RecyclerItemClickListenr.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                //todo: Claim the ticket 1
                claimTicket1(keyModel.gameType, position)
            }
            override fun onItemLongClick(view: View?, position: Int) {
                TODO("Not yet implemented")
            }
        }))
    }
    private fun onRecyclerViewClaimTicket2(){
        // Initializing an empty ArrayList to be filled with items
        var prizeList: List<String> = emptyList()
        if (keyModel.gameType == 1){
            // todo: game type: cash
            prizeList = resources.getStringArray(R.array.cash_claim_ticket_list).asList()
            rVClaimTicket2.layoutManager = GridLayoutManager(context,2)
        }else if (keyModel.gameType == 2){
            // todo: game type: tournament
            prizeList = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()
            rVClaimTicket2.layoutManager = GridLayoutManager(context,3)
        }else {
            // todo: game type: sample
            prizeList = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()
            rVClaimTicket2.layoutManager = GridLayoutManager(context,3)
        }
        val adapter = ClaimTicketAdapter(prizeList,this)
        rVClaimTicket2.adapter = adapter
        rVClaimTicket2.addOnItemTouchListener(RecyclerItemClickListenr(context as PlayActivity, rVTicket1, object : RecyclerItemClickListenr.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                //todo: Claim the ticket 2
                claimTicket2(keyModel.gameType, position)
            }
            override fun onItemLongClick(view: View?, position: Int) {
                TODO("Not yet implemented")
            }
        }))
    }
    private fun claimTicket1(gameType: Int, position: Int){
        when (gameType) {
            0 -> {
                // todo: sample game
                val claim = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()[position]
            }
            1 -> {
                // todo: cash game
                val claim = resources.getStringArray(R.array.cash_claim_ticket_list).asList()[position]
            }
            2 -> {
                // todo: tournament game
                val claim = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()[position]
            }
        }
    }
    private fun claimTicket2(gameType: Int, position: Int){
        when (gameType) {
            0 -> {
                // todo: sample game
                val claim = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()[position]
            }
            1 -> {
                // todo: cash game
                val claim = resources.getStringArray(R.array.cash_claim_ticket_list).asList()[position]
            }
            2 -> {
                // todo: tournament game
                val claim = resources.getStringArray(R.array.tournament_claim_ticket_list).asList()[position]
            }
        }
    }
    private fun onRecyclerViewNumberGrid(){
        var numList: MutableList<String> = mutableListOf<String>()
        for (i in 1..90) {
            //println(i)
            numList.add(i.toString())
        }
        // Initializing an empty ArrayList to be filled with items
        val adapter = NumberGridAdapter(numList,this)
        recyclerViewNoGrid.layoutManager = GridLayoutManager(context,3)
        recyclerViewNoGrid.adapter = adapter
    }
    private fun rand(from: Int, to: Int) : Int {
        return random.nextInt(to - from) + from
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK ->  {
                // do something here
                context?.let { dialogExit(it) }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
    private fun dialogExit(context: Context){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Do you want to exit?")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->

            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra(HomeActivity.KEY_LOGIN, SharedPrefManager.getInstance(context).result)
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            //Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.show()
    }
}
