package com.newitzone.desitambola

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Parcelable
import android.text.format.Time
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.newitzone.desitambola.PlayTournamentGameActivity.Companion.BOTTOM_LINE
import com.newitzone.desitambola.PlayTournamentGameActivity.Companion.EARLY_FIVE
import com.newitzone.desitambola.PlayTournamentGameActivity.Companion.FOUR_CORNER
import com.newitzone.desitambola.PlayTournamentGameActivity.Companion.FULL_HOUSE
import com.newitzone.desitambola.PlayTournamentGameActivity.Companion.GAME_RESULT
import com.newitzone.desitambola.PlayTournamentGameActivity.Companion.MIDDLE_LINE
import com.newitzone.desitambola.PlayTournamentGameActivity.Companion.TOP_LINE
import com.newitzone.desitambola.PlayTournamentGameActivity.Companion.TOURNAMENT_GAME
import com.newitzone.desitambola.PlayTournamentGameActivity.Companion.TYPE_CLAIM_STATUS
import com.newitzone.desitambola.adapter.TournamentAdapter
import com.newitzone.desitambola.dialog.MessageDialog
import com.newitzone.desitambola.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.KeyModel
import model.login.Result
import model.result.GameResult
import model.tournament.ResTournament
import model.tournament.Tournament
import retrofit.TambolaApiService
import retrofit2.HttpException
import kotlin.math.abs


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class TournamentGameActivity : AppCompatActivity() {
    private var context: Context? = null
    private lateinit var login: Result
    private lateinit var keyModel: KeyModel
    private lateinit var countDownTimer:CountDownTimer
    private var listState: Parcelable? = null
    @BindView(R.id.recycler_view) lateinit var recyclerView: RecyclerView
    private var isProgressBar = true
    private var scrollPosition = 0
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.dialog_tournament_games)
        supportActionBar?.hide()
        this.context = this@TournamentGameActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        // Intent
        login = intent.getSerializableExtra(HomeActivity.KEY_LOGIN) as Result
        keyModel = intent.getSerializableExtra(HomeActivity.KEY_MODEL) as KeyModel
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable("ListState")
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        // start tournament api
        startTournamentApi()
        startTimer(60)
    }

    override fun onPause() {
        super.onPause()
        if (countDownTimer != null) {
            countDownTimer.cancel()
        }
    }
    override fun onStop() {
        super.onStop()
        if (countDownTimer != null) {
            countDownTimer.cancel()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (countDownTimer != null) {
            countDownTimer.cancel()
        }
    }
    private fun startTimer(sec: Long){
        // 60 seconds (1 minute)
        val minute:Long = 1000 * sec // 1000 milliseconds = 1 second
        // Count down interval 1 second
        val countDownInterval: Long = 1000

        countDownTimer = timer(minute,countDownInterval).start()
    }
    private fun timer(millisInFuture:Long,countDownInterval:Long): CountDownTimer {
        return object: CountDownTimer(millisInFuture,countDownInterval){

            override fun onTick(millisUntilFinished: Long){
                //Log.d(TAG, "tick:$millisUntilFinished")
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onFinish() {
                startTournamentApi()
                startTimer(60)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onLoadRecyclerView(context: Context, resTournament: ResTournament){
        // Initializing an empty ArrayList to be filled with items
        val adapter = TournamentAdapter(resTournament.result.list, resTournament.result.date, resTournament.result.day, context)
        recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerView.adapter = adapter
        recyclerView.scrollToPosition(scrollPosition)
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener { recyclerView, position, v ->
            scrollPosition = position
            val tournament = resTournament.result.list[position]
            val tournamentId = resTournament.result.list[position].id
            val gameId = resTournament.result.list[position].game_id
            val amt = resTournament.result.list[position].amount
            val reqOpen = resTournament.result.list[position].requestOpen.toInt()
            val userReqTickets = resTournament.result.list[position].userRequestedTickets.toInt()
            // set tournament id in key model
            keyModel.tournamentId = tournamentId
            keyModel.amount = amt.toFloat()
            // TODO: Automatic time enable checking
            if (UtilMethods.isAutoTime(context) == 1) {
                if (reqOpen == 1) {
                    // TODO: if request is open
                    if (userReqTickets <= 0) {
                        if (login.acBal.toDouble() >= keyModel.amount.toDouble()) {
                            // TODO: Join the tournament
                            // TODO: Call Ticket Activity
                            val intent = Intent(context, TicketActivity::class.java)
                            intent.putExtra(HomeActivity.KEY_LOGIN, login)
                            intent.putExtra(HomeActivity.KEY_MODEL, keyModel)
                            startActivityForResult(intent, REQ_CODE)
                        }else{
                            val context = this@TournamentGameActivity
                            MessageDialog(context,"Low balance", "Insufficient balance in your account to join Tournament game").show()
                        }
                    } else if (userReqTickets > 0) {
                        // TODO: Cancel Request
                        val currentTime = System.currentTimeMillis()
                        val tournamentTime = UtilMethods.getDateInMS(resTournament.result.date + " " + resTournament.result.list[position].startTime)
                        // TODO: Time difference
                        var different = UtilMethods.getTimeDifferenceInMinute(currentTime, tournamentTime!!)

                        if (different < -5) {
                            // TODO: time difference is greater than 5 minutes
                            // TODO: show dialog to cancel request
                            dialogAlertCancel(
                                context,
                                login.id,
                                login.sid,
                                tournamentId,
                                gameId
                            )
                        }
                    }
                } else if (reqOpen == 0) {
                    // TODO: if request is close
                    val currentTime = System.currentTimeMillis()
                    val tournamentTime = UtilMethods.getDateInMS(resTournament.result.date + " " + resTournament.result.list[position].startTime)

                    // TODO: Time difference for play
                    var different = UtilMethods.getTimeDifferenceInMinute(currentTime, tournamentTime!!)
                    if (different <= 2) {
                        if (different >= -2) {
                            // TODO: time difference is 2 minute and less than equal to 2
                            if (userReqTickets > 0) {
                                // TODO: start the game
                                //UtilMethods.ToastLong(context,"Your game is started in several minutes")
                                val intent = Intent(context, LoadingTournamentGameActivity::class.java)
                                intent.putExtra(HomeActivity.KEY_LOGIN, login)
                                intent.putExtra(HomeActivity.KEY_TOURNAMENT, tournament)
                                intent.putExtra(HomeActivity.KEY_TOURNAMENT_TIMER,abs(UtilMethods.getTimeDifferenceInSec(currentTime,tournamentTime!!)))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                startActivity(intent)
                            } else {
                                MessageDialog(
                                    context,
                                    "Alert",
                                    "You have not participate this Tournament"
                                ).show()
                            }
                        } else if (different >= -5 && different < -2) {
                            if (userReqTickets > 0) {
                                // TODO: Time difference for tournament play soon
                                MessageDialog(
                                    context,
                                    "Alert",
                                    "Tournament will start in few minutes, Be ready"
                                ).show()
                            } else {
                                MessageDialog(
                                    context,
                                    "Alert",
                                    "You have not participate this Tournament"
                                ).show()
                            }
                        }
                    } else if (different in 3..14) {
                        // TODO: Time difference for tournament going
                        MessageDialog(context, "Alert", "Please wait...for the result").show()
                    } else if (different > 14) {
                        // TODO: Time difference for tournament result
                        //MessageDialog(context, "Alert", "Result is coming soon.....").show()
                        // TODO: call game claim status api
                        callGamePrizeClaimOrStatusApi(tournament,"","",  TYPE_CLAIM_STATUS)
                    }
                }
            } else {
                MessageDialog(context,"Alert","Please enable automatic time of your device").show()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun dialogAlertCancel(context: Context, userId: String, sesId: String, tournamentId: String, gameId: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Do you want to withdraw with this tournament game?")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            dialog.dismiss()
            cancelRequestTournamentApi(context, userId, sesId, tournamentId, gameId)
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            //Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.show()
    }
    // TODO: tournament Api
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startTournamentApi(){
        val context = context as TournamentGameActivity
        if (login != null && keyModel != null) {
            if (login.id.isNotBlank() && login.sid.isNotBlank()) {
                tournamentApi(context, login.id, login.sid)
            }else{
                finish()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun tournamentApi(context: Context, userId: String, sesId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            if(isProgressBar){ UtilMethods.showLoading(context, true)}
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.getTournament(userId, sesId)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body()?.status == 1) {
                                    // TODO: Call to load RecyclerView
                                    onLoadRecyclerView(context, response.body()!!)
                                }else{
                                    UtilMethods.ToastLong(context,"Msg:${response.body()?.msg}")
                                }
                            } else {
                                UtilMethods.ToastLong(context,"Error: ${response.code()}" + "\nMsg:${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")

                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
                        }
                        UtilMethods.hideLoading()
                    }
                }catch (e: Throwable) {
                    Log.e("TAG","Throwable : $e")
                    UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
    // TODO: Tournament Start Json by local
    private fun getTournamentStartJson(): ResTournament {
        val jsonFileString = context?.let { Constants.getJsonDataFromAsset(it, "tournamentList.json") }
        Log.i("data", jsonFileString)

        val gson = Gson()
        val tournament = object : TypeToken<ResTournament>() {}.type

        return gson.fromJson(jsonFileString, tournament)
    }
    // TODO: cancel request tournament Api
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun cancelRequestTournamentApi(context: Context, userId: String, sesId: String, tournament_id: String, game_id: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.gameRequestTournamentCancel(userId, sesId, tournament_id, game_id)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body()?.status == 1) {
                                    UtilMethods.hideLoading()
                                    // TODO: cancel request response
                                    UtilMethods.ToastLong(context,"${response.body()?.msg}")
                                    startTournamentApi()
                                }else{
                                    UtilMethods.ToastLong(context,"${response.body()?.msg}")
                                }
                            } else {
                                UtilMethods.ToastLong(context,"Error: ${response.code()}" + "\nMsg:${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")

                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
                        }
                        UtilMethods.hideLoading()
                    }
                }catch (e: Throwable) {
                    Log.e("TAG","Throwable : $e")
                    UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
    // TODO: Prize claiming api
    private fun callGamePrizeClaimOrStatusApi(tournament: Tournament, claimAmount: String,claimType: String, type: Int){
        val context= this@TournamentGameActivity
        val userId = login.id
        val sessionId = login.sid
        if (sessionId.isNotBlank()) {
            if (tournament.game_id != null && tournament.game_id.isNotBlank()) {
                //TODO: Use this
                gamePrizeClaimOrStatusApi(
                    context,
                    userId,
                    sessionId,
                    PlayActivity.TOURNAMENT_GAME.toString(),
                    claimAmount,
                    tournament.id,
                    tournament.req_id,
                    tournament.game_id,
                    claimType,
                    type
                )
            }else{
                MessageDialog(context , "Result alert","No one participated in this Tournament").show()
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
                                    // TODO: Calculate result
                                    onResultCalculate(response.body()!!.result)
                                    //UtilMethods.ToastLong(context, "${response.body()?.msg}")
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
    private fun onResultCalculate(claimList: List<model.claim.Result>){
        var gameResult = GameResult(emptyList())
        if (claimList != null){
            var resultList: ArrayList<model.result.Result> = arrayListOf()
            for (elements in claimList){
                if (elements.claimType == TOP_LINE.toString()){
                    resultList.add(model.result.Result(1, elements.userId, elements.userNm, "Top Line", elements.amt.toDouble(), TOURNAMENT_GAME))
                }else if (elements.claimType == BOTTOM_LINE.toString()){
                    resultList.add(model.result.Result(2, elements.userId, elements.userNm, "Bottom Line", elements.amt.toDouble(), TOURNAMENT_GAME))
                }else if (elements.claimType == MIDDLE_LINE.toString()){
                    resultList.add(model.result.Result(3, elements.userId, elements.userNm, "Middle Line", elements.amt.toDouble(), TOURNAMENT_GAME))
                }else if (elements.claimType == FULL_HOUSE.toString()){
                    resultList.add(model.result.Result(4, elements.userId, elements.userNm, "Full House", elements.amt.toDouble(), TOURNAMENT_GAME))
                }else if (elements.claimType == FOUR_CORNER.toString()){
                    resultList.add(model.result.Result(5, elements.userId, elements.userNm, "4 Corners", elements.amt.toDouble(), TOURNAMENT_GAME))
                }else if (elements.claimType == EARLY_FIVE.toString()){
                    resultList.add(model.result.Result(6, elements.userId, elements.userNm, "Early 5", elements.amt.toDouble(), TOURNAMENT_GAME))
                }
            }
            if (gameResult != null){
                gameResult.resultList = resultList
                // TODO: Go to result screen to display result
                onGotoResultScreen(gameResult)
            }
        }
    }
    private fun onGotoResultScreen(gameResult: GameResult){
        val intent = Intent(context, ResultActivity::class.java)
        intent.putExtra(GAME_RESULT, gameResult)
        intent.putExtra(ResultActivity.KEY_RESULT, ResultActivity.RESULT_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE && data != null) {
            isProgressBar = false
            startTournamentApi()
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("ListState", recyclerView.layoutManager?.onSaveInstanceState())
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK ->  {
                // do something here
                if (countDownTimer != null) {
                    countDownTimer.cancel()
                }
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
    companion object {
        const val TAG = "TournamentScreen"
        const val REQ_CODE = 100
    }

}
