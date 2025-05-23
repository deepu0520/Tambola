package com.newitzone.desitambola

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.newitzone.desitambola.adapter.*
import com.newitzone.desitambola.dialog.MessageDialog
import com.newitzone.desitambola.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.NumModel
import model.claim.PrizeModel
import model.gamein.UserData
import model.result.GameResult
import model.result.Result
import model.tournament.start.ResTournamentStart
import retrofit.TambolaApiService
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class PlayTournamentGameActivity : AppCompatActivity(){
    private var context: Context? = null
    private val DELAY_MS_GAME_OVER: Long = 10000  //delay in milliseconds before task is to be executed
    private val DELAY_MS: Long = 2000  //delay in milliseconds before task is to be executed
    private val second: Long = 1000 * 30 // 1000 milliseconds into 1 second
    private val countDownInterval: Long = 1000
    private lateinit var login: model.login.Result
    private lateinit var tournamentStart: model.tournament.start.Result
    private lateinit var userData: UserData
    private val random = Random()
    private var claimTicket1 = true
    private var claimTicket2 = true
    private var i = 0
    private var posRanNum = 0
    private val handler = Handler()
    private var randomNumList: MutableList<String> = mutableListOf<String>()
    private val numGridList: MutableList<NumModel> = mutableListOf<NumModel>()
    private val numList1: MutableList<NumModel> = mutableListOf()
    private val numList2: MutableList<NumModel> = mutableListOf()
    private var prizeList1: MutableList<PrizeModel> = mutableListOf()
    private var prizeList2: MutableList<PrizeModel> = mutableListOf()
    private var isActivity = false;
    private var isThreadRunning = true
    private var isNotifyTop = false
    private var isNotifyBottom = false
    private var isNotifyMiddle = false
    private var isNotify4Corner = false
    private var isNotifyEarly5 = false
    private var isNotifyFullHouse = false
    private var gameResult = GameResult(emptyList())
    private var claimPrizeList: List<model.claim.Result> = arrayListOf()

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

    @BindView(R.id.content) lateinit var consLayout: ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_play)
        supportActionBar?.hide()
        this.context = this@PlayTournamentGameActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        login = intent.getSerializableExtra(HomeActivity.KEY_LOGIN) as model.login.Result
        val tStart = intent.getSerializableExtra(LoadingTournamentGameActivity.KEY_TOURNAMENT_START) as ResTournamentStart
        //keyGameInResFixUser = intent.getSerializableExtra(LoadingActivity.KEY_GAME_IN_FIX_USER) as FixUserGameIn
        if (login != null && tStart != null) {
            tournamentStart = tStart.result[0]
            // TODO: Ticket display by user
            if (tournamentStart.ticketCount == "1") {
                lLTicket1.visibility = View.VISIBLE
                lLTicket2.visibility = View.GONE
            } else if (tournamentStart.ticketCount == "2") {
                lLTicket1.visibility = View.VISIBLE
                lLTicket2.visibility = View.VISIBLE
            }
            // TODO: Number Grid
            onRecyclerViewNumberGrid()
            // TODO: Random Number open
            onRecyclerViewRandomNumber()
            // TODO: Live User
            onRecyclerViewLiveUser()
            // TODO: Ticket 1
            onTicket(TICKET_TYPE_1)
            // TODO: Ticket 2
            onTicket(TICKET_TYPE_2)
            // TODO: Load Recycler view prize Ticket 1
            onLoadRecyclerViewPrize(TICKET_TYPE_1)
            // TODO: Load Recycler view prize Ticket 2
            onLoadRecyclerViewPrize(TICKET_TYPE_2)
            // TODO: Show/hide prize Ticket 1
            tvBtnClaimTicket1.setOnClickListener {
                onTicketPrizeShowHide(TICKET_TYPE_1)
            }
            // TODO: Show/hide prize Ticket 2
            tvBtnClaimTicket2.setOnClickListener {
                onTicketPrizeShowHide(TICKET_TYPE_2)
            }
        }
    }
    // TODO: Random number
    private fun onRecyclerViewRandomNumber(){
        if (isThreadRunning) {

            if (tournamentStart != null) {
                // TODO: Init random number
                var ranNum = tournamentStart.randNo[posRanNum]
                // TODO: add new randum number into list
                randomNumList.add(ranNum.toString())
                // TODO: set sound of randum number
                runOnUiThread { startSound("sounds/$ranNum.mp3") }
                // TODO: Init progress bar
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
                            Log.e(TAG, "Thread Exception : " + e.printStackTrace());
                        }
                    }
                    handler.post(Runnable {
                        // Initializing an empty ArrayList to be filled with items
                        val adapter = RandomNumberAdapter(randomNumList, this)
                        recyclerViewRanNum.layoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                        recyclerViewRanNum.adapter = adapter
                        recyclerViewRanNum.scrollToPosition(randomNumList.size - 1)
                        // TODO: update ticket1 value
                        for ((i, elements) in numList1.withIndex()) {
                            if (elements.num != 0) {
                                if (elements.isChecked) {
                                    numList1[i].isChecked = randomNumList.contains(elements.num.toString())
                                    numList1[i].isTick = numList1[i].isChecked
                                }
                            }
                        }
                        rVTicket1.adapter?.notifyDataSetChanged()
                        // TODO: update ticket2 value
                        for ((i, elements) in numList2.withIndex()) {
                            if (elements.num != 0) {
                                if (elements.isChecked) {
                                    numList2[i].isChecked = randomNumList.contains(elements.num.toString())
                                    numList2[i].isTick = numList2[i].isChecked
                                }
                            }
                        }
                        rVTicket2.adapter?.notifyDataSetChanged()
                        // TODO: call game claim status api
                        callGamePrizeClaimOrStatusApi("","", TYPE_CLAIM_STATUS)

                        if (randomNumList.size < 90) {
                            posRanNum++
                            // restart
                            onRecyclerViewRandomNumber()
                        } else {
                            rLProBar.visibility = View.INVISIBLE
                            isThreadRunning = false
                            if (isActivity) {
                                context?.let {
                                    MessageDialog(
                                        it,
                                        "Tournament over alert",
                                        "Tournament will over within 10 seconds. So please claim your prize immediately"
                                    ).show()
                                }
                                // handler
                                Handler().postDelayed({
                                    // TODO: Calculate result
                                    if (claimPrizeList != null) {
                                        onResultCalculate(tournamentStart.gameValue[0],claimPrizeList)
                                    }
                                }, DELAY_MS_GAME_OVER)
                            }
                        }
                    })
                }).start()
            }
        }
    }

    // TODO: Live User
    private fun onRecyclerViewLiveUser(){
        if (tournamentStart != null) {
            // Initializing an empty ArrayList to be filled with items
//            val list: List<UserData> = tournamentStart.result.userData
//            val adapter = LiveUserAdapter(list, this)
//            recyclerViewLiveUser.layoutManager = LinearLayoutManager(context)
//            recyclerViewLiveUser.adapter = adapter
//            recyclerViewLiveUser.addOnItemTouchListener(RecyclerItemClickListenr(this, recyclerViewLiveUser,
//                    object : RecyclerItemClickListenr.OnItemClickListener {
//
//                        override fun onItemClick(view: View, position: Int) {
//                            //TODO: Use this
//                        }
//
//                        override fun onItemLongClick(view: View?, position: Int) {
//                            TODO("do nothing")
//                        }
//                    })
//            )
        }
    }

    // TODO: Number grid
    private fun onRecyclerViewNumberGrid(){
        for (i in 1..90) {
            numGridList.add(NumModel(i,false,false))
        }
        // Initializing an empty ArrayList to be filled with items
        val adapter = NumberGridAdapter(numGridList,this)
        recyclerViewNoGrid.layoutManager = GridLayoutManager(context,3)
        recyclerViewNoGrid.adapter = adapter
    }

    // TODO: Ticket and Prize
    private fun onTicket(ticketType: Int){
        val context = this@PlayTournamentGameActivity
        when(ticketType){
            TICKET_TYPE_1 ->{
                // TODO: Ticket 1
                if (tournamentStart != null && tournamentStart.tickets.ticket1 != null && tournamentStart.tickets.ticket1.isNotEmpty()) {
                    val listRow1 = tournamentStart.tickets.ticket1[0] as List<Int>
                    for (element in listRow1) {
                        val numModel = NumModel(element,false,false)
                        numList1.add(numModel)
                    }
                    val listRow2 = tournamentStart.tickets.ticket1[1] as List<Int>
                    for (element in listRow2) {
                        val numModel = NumModel(element,false,false)
                        numList1.add(numModel)
                    }
                    val listRow3 = tournamentStart.tickets.ticket1[2] as List<Int>
                    for (element in listRow3) {
                        val numModel = NumModel(element,false,false)
                        numList1.add(numModel)
                    }
                    val adapter = TicketNumberAdapter(numList1, this)
                    rVTicket1.layoutManager = GridLayoutManager(context, 9)
                    rVTicket1.adapter = adapter
                }
            }
            TICKET_TYPE_2 ->{
                // TODO: Ticket 2
                if (tournamentStart != null && tournamentStart.tickets.ticket2 != null && tournamentStart.tickets.ticket2.isNotEmpty() && tournamentStart.gameType == "2") {
                    val listRow1 = tournamentStart.tickets.ticket2[0]
                    for (element in listRow1) {
                        val numModel = NumModel(element,false,false)
                        numList2.add(numModel)
                    }
                    val listRow2 = tournamentStart.tickets.ticket2[1]
                    for (element in listRow2) {
                        val numModel = NumModel(element,false,false)
                        numList2.add(numModel)
                    }
                    val listRow3 = tournamentStart.tickets.ticket2[2]
                    for (element in listRow3) {
                        val numModel = NumModel(element,false,false)
                        numList2.add(numModel)
                    }
                    val adapter = TicketNumberAdapter(numList2, this)
                    rVTicket2.layoutManager = GridLayoutManager(context, 9)
                    rVTicket2.adapter = adapter
                }
            }
        }
    }
    private fun onTicketPrizeShowHide(ticketType: Int) {
        when(ticketType) {
            TICKET_TYPE_1 -> {
                // TODO: Ticket 1
                if (claimTicket1) {
                    rVClaimTicket1.visibility = View.VISIBLE
                    claimTicket1 = false
                    tvBtnClaimTicket1.text = resources.getString(R.string.txt_ticket_hide_1)
                } else {
                    rVClaimTicket1.visibility = View.GONE
                    claimTicket1 = true
                    tvBtnClaimTicket1.text = resources.getString(R.string.txt_ticket_1)
                }
            }
            TICKET_TYPE_2 -> {
                // TODO: Ticket 2
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
        }
    }
    private fun onLoadRecyclerViewPrize(ticketType: Int){
        // todo: game type: tournament(2)
        for((i,elements) in resources.getStringArray(R.array.tournament_claim_ticket_list).asList().withIndex()) {
            val prizeModel = PrizeModel(i,elements,"","",false)
            if (ticketType == TICKET_TYPE_1) {
                prizeList1.add(prizeModel)
            }else if (ticketType == TICKET_TYPE_2) {
                prizeList2.add(prizeModel)
            }
        }
        if (ticketType == TICKET_TYPE_1) {
            rVClaimTicket1.layoutManager = GridLayoutManager(context,3)
        }else if (ticketType == TICKET_TYPE_2) {
            rVClaimTicket2.layoutManager = GridLayoutManager(context,3)
        }

        if (ticketType == TICKET_TYPE_1) {
            // TODO: Ticket 1
            val adapter = ClaimTicketAdapter(prizeList1,this)
            rVClaimTicket1.adapter = adapter
            ItemClickSupport.addTo(rVClaimTicket1).setOnItemClickListener { recyclerView, position, v ->
                if (!prizeList1[position].isChecked) {
                    //todo: Claim the ticket 1
                    onPrizeClaim(ticketType, position, prizeList1)
                }
            }
        }else if (ticketType == TICKET_TYPE_2) {
            // TODO: Ticket 2
            val adapter = ClaimTicketAdapter(prizeList2,this)
            rVClaimTicket2.adapter = adapter
            ItemClickSupport.addTo(rVClaimTicket2).setOnItemClickListener { recyclerView, position, v ->
                if (!prizeList2[position].isChecked) {
                    //todo: Claim the ticket 2
                    onPrizeClaim(ticketType, position, prizeList2)
                }
            }
        }
    }
    private fun onPrizeClaim(ticketType: Int, position: Int, prizeList: MutableList<PrizeModel>){
        // todo: if prize is not claimed then claimed it
        if (prizeList[position].id == TOP_LINE){
            if (!prizeList[position].isChecked){
                claimPrize(TOP_LINE,ticketType,position)
            }else{
                context?.let { UtilMethods.ToastLong(it,"Already claimed Top line") }
            }
        }else if (prizeList[position].id == BOTTOM_LINE){
            if (!prizeList[position].isChecked){
                claimPrize(BOTTOM_LINE,ticketType,position)
            }else{
                context?.let { UtilMethods.ToastLong(it,"Already claimed Bottom line") }
            }
        }else if (prizeList[position].id == MIDDLE_LINE){
            if (!prizeList[position].isChecked){
                claimPrize(MIDDLE_LINE,ticketType,position)
            }else{
                context?.let { UtilMethods.ToastLong(it,"Already claimed Middle line") }
            }
        }else if (prizeList[position].id == FOUR_CORNER){
            if (!prizeList[position].isChecked){
                claimPrize(FOUR_CORNER,ticketType,position)
            }else{
                context?.let { UtilMethods.ToastLong(it,"Already claimed Four Corners") }
            }
        }
        else if (prizeList[position].id == EARLY_FIVE){
            if (!prizeList[position].isChecked){
                claimPrize(EARLY_FIVE,ticketType,position)
            }else{
                context?.let { UtilMethods.ToastLong(it,"Already claimed Early Five") }
            }
        }
        else if (prizeList[position].id == FULL_HOUSE){
            if (!prizeList[position].isChecked){
                claimPrize(FULL_HOUSE,ticketType,position)
            }else{
                context?.let { UtilMethods.ToastLong(it,"Already claimed Full House") }
            }
        }
    }
    private fun claimPrize(claimType: Int, ticketType: Int, position: Int){

        var numList: MutableList<NumModel> = mutableListOf()
        if (ticketType == TICKET_TYPE_1){
            numList = numList1
        }else if (ticketType == TICKET_TYPE_2){
            numList = numList2
        }
        // TODO: claim Type
        when(claimType){
            TOP_LINE    ->{
                try {
                    var tNum = 0
                    var tCheckedNum = 0
                    for((i, elements) in numList.withIndex()){
                        if (i in 0..8) {
                            if (elements.num != 0) {
                                if (elements.isChecked) {
                                    if (randomNumList.contains(elements.num.toString())) {
                                        tCheckedNum++
                                    }
                                }
                                tNum++
                            }
                        }
                    }
                    if (tNum == tCheckedNum){
                        context?.let { UtilMethods.ToastLong(it,"You claimed Top line") }
                        // TODO: call game claim status api
                        callGamePrizeClaimOrStatusApi(getClaimValueByPrize(TOP_LINE),TOP_LINE.toString(), TYPE_CLAIM_PRIZE)
                        // TODO: refresh prize claim
                        onRefreshPrizeClaim(ticketType, position)
                    }else{
                        context?.let { UtilMethods.ToastLong(it,"You claimed Invalid") }
                        //setViewAndChildrenEnabled(consLayout,false)
                    }
                } catch (e: Exception) {
                    Log.e("Claim Top Line","Exception: {$e}")
                }
            }
            BOTTOM_LINE ->{
                try {
                    var tNum = 0
                    var tCheckedNum = 0
                    for((i, elements) in numList.withIndex()){
                        if (i in 18..26) {
                            if (elements.num != 0) {
                                if (elements.isChecked) {
                                    if (randomNumList.contains(elements.num.toString())) {
                                        tCheckedNum++
                                    }
                                }
                                tNum++
                            }
                        }
                    }
                    if (tNum == tCheckedNum){
                        context?.let { UtilMethods.ToastLong(it,"You claimed Bottom line") }
                        // TODO: call game claim status api
                        callGamePrizeClaimOrStatusApi(getClaimValueByPrize(BOTTOM_LINE), BOTTOM_LINE.toString(), TYPE_CLAIM_PRIZE)
                        // TODO: refresh prize claim
                        onRefreshPrizeClaim(ticketType, position)
                    }else{
                        context?.let { UtilMethods.ToastLong(it,"You claimed Invalid") }
                    }
                } catch (e: Exception) {
                    Log.e("Claim Bottom Line","Exception: {$e}")
                }
            }
            MIDDLE_LINE ->{
                try {
                    var tNum = 0
                    var tCheckedNum = 0
                    for((i, elements) in numList.withIndex()){
                        if (i in 9..17) {
                            if (elements.num != 0) {
                                if (elements.isChecked) {
                                    if (randomNumList.contains(elements.num.toString())) {
                                        tCheckedNum++
                                    }
                                }
                                tNum++
                            }
                        }
                    }
                    if (tNum == tCheckedNum){
                        context?.let { UtilMethods.ToastLong(it,"You claimed Middle line") }
                        // TODO: call game claim status api
                        callGamePrizeClaimOrStatusApi(getClaimValueByPrize(MIDDLE_LINE),MIDDLE_LINE.toString(), TYPE_CLAIM_PRIZE)
                        // TODO: refresh prize claim
                        onRefreshPrizeClaim(ticketType, position)
                    }else{
                        context?.let { UtilMethods.ToastLong(it,"You claimed Invalid") }
                        //setViewAndChildrenEnabled(consLayout,false)
                    }
                } catch (e: Exception) {
                    Log.e("Claim Middle Line","Exception: {$e}")
                }
            }
            FOUR_CORNER ->{
                try {
                    var topLeft = false
                    var topRight = false
                    var bottomLeft = false
                    var bottomRight = false
                    // TODO: Top Left
                    for((i, elements) in numList.withIndex()){
                        if (i in 0..3) {
                            if (elements.num != 0) {
                                if (elements.isChecked) {
                                    topLeft = randomNumList.contains(elements.num.toString())
                                }
                                break
                            }
                        }
                    }
                    // TODO: Top Right
                    for((i, elements) in numList.asReversed().withIndex()){
                        if (i in 18..21) {
                            if (elements.num != 0) {
                                if (elements.isChecked) {
                                    topRight = randomNumList.contains(elements.num.toString())
                                }
                                break
                            }
                        }
                    }
                    // TODO: Bottom Left
                    for((i, elements) in numList.withIndex()){
                        if (i in 18..21) {
                            if (elements.num != 0) {
                                if (elements.isChecked) {
                                    bottomLeft = randomNumList.contains(elements.num.toString())
                                }
                                break
                            }
                        }
                    }
                    // TODO: Bottom Right
                    for((i, elements) in numList.asReversed().withIndex()){
                        if (i in 0..3) {
                            if (elements.num != 0) {
                                if (elements.isChecked) {
                                    bottomRight = randomNumList.contains(elements.num.toString())
                                }
                                break
                            }
                        }
                    }
                    // TODO: Check condition 4 corners
                    if (topLeft && topRight && bottomLeft && bottomRight){
                        context?.let { UtilMethods.ToastLong(it,"You claimed Four corners") }
                        // TODO: call game claim status api
                        callGamePrizeClaimOrStatusApi(getClaimValueByPrize(FOUR_CORNER),FOUR_CORNER.toString(), TYPE_CLAIM_PRIZE)
                        // TODO: refresh prize claim
                        onRefreshPrizeClaim(ticketType, position)
                    }else{
                        context?.let { UtilMethods.ToastLong(it,"You claimed Invalid") }
                        //setViewAndChildrenEnabled(consLayout,false)
                    }
                } catch (e: Exception) {
                    Log.e("Claim Four corners","Exception: {$e}")
                }
            }
            EARLY_FIVE ->{
                try {
                    var tNum = 5
                    var tCheckedNum = 0
                    for((i, elements) in numList.withIndex()){
                        if (i in 0..26) {
                            if (elements.num != 0) {
                                if (elements.isChecked) {
                                    if (randomNumList.contains(elements.num.toString())) {
                                        tCheckedNum++
                                    }
                                }
                            }
                        }
                    }
                    if (tCheckedNum >= tNum){
                        context?.let { UtilMethods.ToastLong(it,"You claimed Early Five") }
                        // TODO: call game claim status api
                        callGamePrizeClaimOrStatusApi(getClaimValueByPrize(EARLY_FIVE),EARLY_FIVE.toString(), TYPE_CLAIM_PRIZE)
                        // TODO: refresh prize claim
                        onRefreshPrizeClaim(ticketType, position)
                    }else{
                        context?.let { UtilMethods.ToastLong(it,"You claimed Invalid") }
                    }
                } catch (e: Exception) {
                    Log.e("Claim Early Five","Exception: {$e}")
                }
            }
            FULL_HOUSE ->{
                try {
                    var tNum = 0
                    var tCheckedNum = 0
                    for((i, elements) in numList.withIndex()){
                        if (i in 0..26) {
                            if (elements.num != 0) {
                                if (elements.isChecked) {
                                    if (randomNumList.contains(elements.num.toString())) {
                                        tCheckedNum++
                                    }
                                }
                                tNum++
                            }
                        }
                    }
                    if (tNum == tCheckedNum){
                        context?.let { UtilMethods.ToastLong(it,"You claimed Full House") }
                        // TODO: call game claim status api
                        callGamePrizeClaimOrStatusApi(getClaimValueByPrize(FULL_HOUSE),FULL_HOUSE.toString(), TYPE_CLAIM_PRIZE)
                        // TODO: refresh prize claim
                        onRefreshPrizeClaim(ticketType, position)

                        // TODO: Goto Result Screen
                        //onGotoResultScreen()
                    }else{
                        context?.let { UtilMethods.ToastLong(it,"You claimed Invalid") }
                    }
                } catch (e: Exception) {
                    Log.e("Claim Full house Line","Exception: {$e}")
                }
            }
        }
    }
    private fun onRefreshPrizeClaim(ticketType: Int,position: Int){
        if (ticketType == TICKET_TYPE_1){
            prizeList1[position].isChecked = true
            rVClaimTicket1.adapter?.notifyDataSetChanged()
        }else if (ticketType == TICKET_TYPE_2){
            prizeList2[position].isChecked = true
            rVClaimTicket2.adapter?.notifyDataSetChanged()
        }
    }

    private fun getClaimValueByPrize(claimType: Int) : String{
        when (claimType) {
            TOP_LINE -> {
                return tournamentStart.gameValue[0].topLine.toString()
            }
            BOTTOM_LINE -> {
                return tournamentStart.gameValue[0].botLine.toString()
            }
            MIDDLE_LINE -> {
                return tournamentStart.gameValue[0].midLine.toString()
            }
            FOUR_CORNER -> {
                return tournamentStart.gameValue[0].fourCor.toString()
            }
            EARLY_FIVE -> {
                return tournamentStart.gameValue[0].earlyFive.toString()
            }
            FULL_HOUSE -> {
                return tournamentStart.gameValue[0].fullHouse.toString()
            }
            else -> return "0.0"
        }
    }
    // TODO: Result
    private fun onResultCalculate(gameValue: model.tournament.start.GameValue, claimList: List<model.claim.Result>){
        isThreadRunning = false
        if (gameValue != null && claimList != null){
            var resultList: ArrayList<Result> = arrayListOf()
            for (elements in claimList){
                if (elements.claimType == TOP_LINE.toString() && gameValue.topLine > 0){
                    resultList.add(Result(1, elements.userId, elements.userNm, "Top Line", gameValue.topLine, tournamentStart.gameType.toInt()))
                }else if (elements.claimType == BOTTOM_LINE.toString() && gameValue.botLine > 0){
                    resultList.add(Result(2, elements.userId, elements.userNm, "Bottom Line", gameValue.botLine, tournamentStart.gameType.toInt()))
                }else if (elements.claimType == MIDDLE_LINE.toString() && gameValue.midLine > 0){
                    resultList.add(Result(3, elements.userId, elements.userNm, "Middle Line", gameValue.midLine, tournamentStart.gameType.toInt()))
                }else if (elements.claimType == FULL_HOUSE.toString() && gameValue.fullHouse > 0){
                    resultList.add(Result(4, elements.userId, elements.userNm, "Full House", gameValue.fullHouse, tournamentStart.gameType.toInt()))
                }else if (elements.claimType == FOUR_CORNER.toString() && gameValue.fourCor > 0){
                    resultList.add(Result(5, elements.userId, elements.userNm, "4 Corners", gameValue.fourCor, tournamentStart.gameType.toInt()))
                }else if (elements.claimType == EARLY_FIVE.toString() && gameValue.earlyFive > 0){
                    resultList.add(Result(6, elements.userId, elements.userNm, "Early 5", gameValue.earlyFive, tournamentStart.gameType.toInt()))
                }
            }
            if (gameResult != null){
                gameResult.resultList = resultList
                // TODO: Go to result screen to display result
                onGotoResultScreen()
            }
        }
    }
    private fun onGotoResultScreen(){
        // Game Over
        setContentView(R.layout.game_over)
        // handler
        Handler().postDelayed({
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(GAME_RESULT, gameResult)
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }, DELAY_MS)
    }
    private fun startSound(filename: String) {
        var afd: AssetFileDescriptor? = null
        try {
            afd = resources.assets.openFd(filename)
        } catch (e: IOException) {
            //e.printStackTrace()
            Log.e(TAG, "Assets Exception : $e")
        }
        val player = MediaPlayer()
        try {
            assert(afd != null)
            player.setDataSource(afd!!.fileDescriptor, afd.startOffset, afd.length)
        } catch (e: IOException) {
            //e.printStackTrace()
            Log.e(TAG, "Media player Data Source Exception : $e")
        }
        try {
            player.prepare()
        } catch (e: IOException) {
            //e.printStackTrace()
            Log.e(TAG, "Media player prepare Exception : $e")
        }
        player.start()
    }

    override fun onStart() {
        super.onStart()
        isActivity = true
    }
    override fun onStop() {
        super.onStop()
        isActivity = false
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

    // TODO: Game exit dialog
    private fun dialogExit(context: Context){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Do you want to exit?")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            isThreadRunning = false
            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra(HomeActivity.KEY_LOGIN, DesiTambolaPreferences.getLogin(context))
            startActivity(intent)
            finish()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            //Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.show()
    }

    // TODO: Prize claiming api
    private fun callGamePrizeClaimOrStatusApi(claimAmount: String,claimType: String, type: Int){
        val context= this@PlayTournamentGameActivity
        val userId = login.id
        val sessionId = login.sid
        if (sessionId.isNotBlank()) {
            if (tournamentStart.gameId.isNotBlank()) {
                //TODO: Use this
                gamePrizeClaimOrStatusApi(
                    context,
                    userId,
                    sessionId,
                    tournamentStart.gameType,
                    claimAmount,
                    tournamentStart.tournamentId,
                    tournamentStart.id,
                    tournamentStart.gameId,
                    claimType,
                    type
                )
            }
        }else{
            UtilMethods.ToastLong(context,"Session expired")
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
    private fun gamePrizeClaimOrStatusApi(context: Context, userid: String, sesid: String, game_type: String, amt: String
                                          , tournament_id: String, reqid: String, game_id: String, claimType: String, type: Int){
        if (UtilMethods.isConnectedToInternet(context)) {
            //UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = service.gamePrizeClaimOrStatus(userid, sesid, game_type, amt, tournament_id, reqid, game_id, claimType, type)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body()?.status == 1) {
                                    claimPrizeList = response.body()!!.result
                                    var isFullHouse = false
                                    // TODO: Checking claimed prize
                                    for (elements in response.body()!!.result) {
                                        if (elements.claimType == TOP_LINE.toString()) {
                                            onUpdatePrizeClaimed(TOP_LINE, elements)
                                            if (!isNotifyTop){
                                                isNotifyTop = true
                                                KCustomToast.toastWithFont(context as Activity,"Top line claimed by ${elements.userNm}")
//                                                KCustomToast.toastWithFont(context as Activity,
//                                                    "Claimed  is ${getClaimValueByPrize(TOP_LINE)} by ${elements.userNm}")
                                            }
                                        } else if (elements.claimType == BOTTOM_LINE.toString()) {
                                            onUpdatePrizeClaimed(BOTTOM_LINE, elements)
                                            if (!isNotifyBottom){
                                                isNotifyBottom = true
                                                KCustomToast.toastWithFont(context as Activity,"Bottom line claimed by ${elements.userNm}")
                                            }
                                        } else if (elements.claimType == MIDDLE_LINE.toString()) {
                                            onUpdatePrizeClaimed(MIDDLE_LINE, elements)
                                            if (!isNotifyMiddle){
                                                isNotifyMiddle = true
                                                KCustomToast.toastWithFont(context as Activity,"Middle line claimed by ${elements.userNm}")
                                            }
                                        } else if (elements.claimType == FOUR_CORNER.toString()) {
                                            onUpdatePrizeClaimed(FOUR_CORNER, elements)
                                            if (!isNotify4Corner){
                                                isNotify4Corner = true
                                                KCustomToast.toastWithFont(context as Activity,"4 corners claimed by ${elements.userNm}")
                                            }
                                        } else if (elements.claimType == EARLY_FIVE.toString()) {
                                            onUpdatePrizeClaimed(EARLY_FIVE, elements)
                                            if (!isNotifyEarly5){
                                                isNotifyEarly5 = true
                                                KCustomToast.toastWithFont(context as Activity,"Early 5 claimed by ${elements.userNm}")
                                            }
                                        } else if (elements.claimType == FULL_HOUSE.toString()) {
                                            if (game_type == "2") {
                                                // todo: game type tournament(2)
                                                onUpdatePrizeClaimed(FULL_HOUSE, elements)
                                                isFullHouse = true
                                            } else {
                                                // todo: game type sample(0) and cash(1)
                                                val fullHouse = 3
                                                onUpdatePrizeClaimed(fullHouse, elements)
                                                isFullHouse = true
                                            }
                                            if (!isNotifyFullHouse){
                                                isNotifyFullHouse = true
                                                KCustomToast.toastWithFont(context as Activity,"Full house claimed by ${elements.userNm}")
                                            }
                                        }
                                    }
                                    // TODO: Refreshed claimed prize list
                                    rVClaimTicket1.adapter?.notifyDataSetChanged()
                                    rVClaimTicket2.adapter?.notifyDataSetChanged()

                                    // TODO: when full house is true
                                    if (isFullHouse){
                                        // handler
                                        Handler().postDelayed({
                                            // TODO: Calculate result
                                            onResultCalculate(tournamentStart.gameValue[0], response.body()!!.result)
                                        }, DELAY_MS)
                                    }
                                } else {
                                    UtilMethods.ToastLong(context, "${response.body()?.msg}")
                                }
                            } else {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                            }
                        } catch (e: HttpException) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")
                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
                        }
                    }
                }catch (e: Throwable) {
                    //runOnUiThread { UtilMethods.ToastLong(context,"Server or Internet error : ${e.message}") }
                    Log.e("TAG","Throwable : $e")
                    //UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
    private fun onUpdatePrizeClaimed(claim: Int, claimResult: model.claim.Result){
        // TODO: checked claim of prize
        prizeList1[claim].isChecked = true
        prizeList2[claim].isChecked = true
        // TODO: set userId claim of prize
        prizeList1[claim].userId = claimResult.userId
        prizeList2[claim].userId = claimResult.userId
        // TODO: set userName claim of prize
        prizeList1[claim].userName = claimResult.userNm
        prizeList2[claim].userName = claimResult.userNm
    }

    // TODO: Companion objects
    companion object {
        const val TAG = "PlayScreen"
        const val TOP_LINE = 0
        const val BOTTOM_LINE = 1
        const val MIDDLE_LINE = 2
        const val FOUR_CORNER = 3
        const val EARLY_FIVE = 4
        const val FULL_HOUSE = 5
        const val TYPE_CLAIM_STATUS = 0
        const val TYPE_CLAIM_PRIZE = 1
        const val TICKET_TYPE_1 = 1
        const val TICKET_TYPE_2 = 2
        const val SAMPLE_GAME = 0
        const val CASH_GAME = 1
        const val TOURNAMENT_GAME = 2
        const val GAME_RESULT = "game_result"
    }
}
