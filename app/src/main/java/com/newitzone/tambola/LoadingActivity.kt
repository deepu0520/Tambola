package com.newitzone.tambola

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.github.anastr.flattimelib.CountDownTimerView
import com.github.anastr.flattimelib.intf.OnTimeFinish
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.newitzone.tambola.utils.Constants
import com.newitzone.tambola.utils.SharedPrefManager
import com.newitzone.tambola.utils.UtilMethods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.KeyModel
import model.fixuser.FixUserGameIn
import model.fixuser.ResFixUser
import model.gamein.GameIn
import model.gamerequeststatus.ResGameReqStatus
import org.json.JSONException
import org.json.JSONObject
import retrofit.TambolaApiService
import retrofit2.HttpException
import java.util.*
import java.util.concurrent.TimeUnit


class LoadingActivity : AppCompatActivity() {
    private var context: Context? = null
    private val DELAY_MS: Long = 4000  //delay in milliseconds before task is to be executed
    private var i = 0
    private val handler = Handler()
    private var timeout = false
    private lateinit var keyModel: KeyModel
    private var grStatus: model.gamerequeststatus.Result? = null
    private var fixUserGameInList: MutableList<GameIn> = mutableListOf<GameIn>()
    private lateinit var countDownTimer:CountDownTimer
    @BindView(R.id.text_message) lateinit var tvMsg: TextView
    @BindView(R.id.text_timer) lateinit var tvTimer: TextView
    @BindView(R.id.countDown_TimerView) lateinit var mCountDownTimer: CountDownTimerView
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_loading)
        supportActionBar?.hide()
        this.context = this@LoadingActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        keyModel = intent.getSerializableExtra(HomeActivity.KEY_MODEL) as KeyModel

        if (keyModel != null){
            tvMsg.text = "Loading..."
            callTimers(60)  // call for 60 sec
            //callGameRequestStatusApiFromCurrentUser(CURRENT_USER)
            // TODO:("Get fix user details")
            callFixUserGameRequestApi()
        }
    }
    private fun callTimers(sec: Long){
        // 60 seconds (1 minute)
        val minute:Long = 1000 * sec // 1000 milliseconds = 1 second
        // Count down interval 1 second
        val countDownInterval: Long = 1000

        countDownTimer = timer(minute,countDownInterval).start()
        countDown(minute)
        //tvMsg.text = "Loading..."
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callGameRequestStatusApi(context: Context, userId: String, sessionId: String, amt: String
                                         , gameType: String, tournamentId: String, gameReqId: String, fixType: Int){
        if (sessionId.isNotBlank()) {
            //TODO: Use this
            volleyGameRequestStatusApi(
                context,
                userId,
                sessionId,
                amt,
                gameType,
                tournamentId,
                gameReqId,
                fixType
            )
        }else{
            UtilMethods.ToastLong(context,"Session expired")
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callGameRequestStatusApiFromCurrentUser(fixType: Int){
        val context= this@LoadingActivity
        val userId = SharedPrefManager.getInstance(context).result.id
        val sessionId = SharedPrefManager.getInstance(context).result.sid
        val amt = keyModel.amount.toString()
        val gameType = keyModel.gameType.toString()
        val tournamentId = keyModel.tournamentId
        val gameReqId = keyModel.gameRequestId
        callGameRequestStatusApi(
            context,
            userId,
            sessionId,
            amt,
            gameType,
            tournamentId,
            gameReqId,
            fixType
        )
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gameRequestStatusApi(context: Context, userId: String, sessionId: String
                                     , amt: String, gameType: String, tournamentId: String, gameRequestId: String,fixType: Int) {
        if (UtilMethods.isConnectedToInternet(context)) {
            //UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.gameRequestStatus(userId, sessionId, gameType, amt, tournamentId,gameRequestId)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.code() == 200) {
                                    if (fixType == CURRENT_USER) {
                                        response.body()?.result?.get(0)?.let { gameInCondition(it) }
                                        grStatus = response.body()?.result?.get(0)!!
                                    }else if (fixType == FIX_USER) {
                                        // TODO: Call Fix user gameIn Api
                                        callGameInApi(context, userId, sessionId, amt, gameType, tournamentId, gameRequestId, fixType)
                                    }
                                } else {
                                    UtilMethods.ToastLong(context, "${response.body()?.msg}")
                                }
                            } else {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")
                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(context,"Ooops: Something else went wrong : " + e.message)
                        }
                    }
                }catch (e: Throwable) {
                    runOnUiThread { UtilMethods.ToastLong(context,"Server or Internet error : ${e.message}") }
                    Log.e("TAG","Throwable : $e")
                    //callGameRequestStatusApiFromCurrentUser(CURRENT_USER)
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun volleyGameRequestStatusApi(context: Context, userId: String, sessionId: String
                                     , amt: String, gameType: String, tournamentId: String, gameRequestId: String,fixType: Int) {
        //getting the record values

        //creating volley string request
        val stringRequest = object : StringRequest(Request.Method.POST, Constants.API_BASE_PATH+"game-request-status",
            Response.Listener<String> { response ->
                try {
                    val jsonObject = JSONObject(response)
                    Log.d(TAG,"Response: $jsonObject")
                    val gson = Gson()
                    val resGameReqStatus = object : TypeToken<ResGameReqStatus>() {}.type

                    var gameReqStatusResponse: ResGameReqStatus = gson.fromJson(response, resGameReqStatus)
                    //Log.d(TAG,"Response: $gameReqStatusResponse")
                    if (fixType == CURRENT_USER) {
                        gameInCondition(gameReqStatusResponse.result[0])
                        grStatus = gameReqStatusResponse.result[0]
                    }else if (fixType == FIX_USER) {
                        // TODO: Call Fix user gameIn Api
                        callGameInApi(context, userId, sessionId, amt, gameType, tournamentId, gameRequestId, fixType)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { volleyError ->
                Toast.makeText(applicationContext, volleyError.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = userId
                params["sesid"] = sessionId
                params["amt"] = amt
                params["game_type"] = gameType
                params["tournament_id"] = tournamentId
                params["reqid"] = gameRequestId
                return params
            }
        }

        //adding request to queue
        VolleySingleton.instance?.addToRequestQueue(stringRequest)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callGameInApiFromCurrentUser(fixType: Int){
        val context= this@LoadingActivity
        val userId = SharedPrefManager.getInstance(context).result.id
        val sessionId = SharedPrefManager.getInstance(context).result.sid
        if (sessionId.isNotBlank()) {
            //TODO: Use this
            callGameInApi(
                context,
                userId,
                sessionId,
                keyModel.amount.toString(),
                keyModel.gameType.toString(),
                keyModel.tournamentId,
                keyModel.gameRequestId,
                fixType
            )
        }else{
            UtilMethods.ToastLong(context,"Session expired")
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callGameInApi(context: Context, userId: String, sessionId: String, amt: String, gameType: String, tournamentId: String, gameRequestId: String, fixType: Int){
        if (sessionId.isNotBlank()) {
            //TODO: Use this
            gameInApi(
                context,
                userId,
                sessionId,
                amt,
                gameType,
                tournamentId,
                gameRequestId,
                fixType
            )
        }else{
            UtilMethods.ToastLong(context,"Session expired")
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gameInApi(context: Context, userid: String, sesid: String
                          , amt: String, game_type: String, tournament_id: String, reqid: String,fixType: Int) {
        if (UtilMethods.isConnectedToInternet(context)) {
            //UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = service.gameIn(userid, sesid, game_type, amt, tournament_id,reqid)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.code() == 200) {
                                    if (fixType == CURRENT_USER) {
                                        // TODO: Current User
                                        //UtilMethods.ToastLong(context, "${response.body()?.msg}")
                                        val gameInResponse = response.body()
                                        if (gameInResponse != null) {
                                            //var fixUserGameInResponse = FixUserGameIn(fixUserGameInList)
                                            // TODO: stop timer
                                            countDownTimer.cancel()
                                            // TODO Auto-generated method stub
                                            val intent = Intent(context, PlayActivity::class.java)
                                            intent.putExtra(HomeActivity.KEY_MODEL, keyModel)
                                            intent.putExtra(HomeActivity.KEY_GAME_IN,gameInResponse)
                                            //intent.putExtra(KEY_GAME_IN_FIX_USER,fixUserGameInResponse)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                            startActivity(intent)
                                            finish()
                                        }
                                    }else if (fixType == FIX_USER){
                                        // TODO: Fix User
                                        fixUserGameInList.add(response.body()!!)

                                        if (fixUserGameInList.size == 3){
                                            // TODO: Call the request status of current user
                                            callGameRequestStatusApiFromCurrentUser(CURRENT_USER)
                                        }
                                    }
                                } else {
                                    UtilMethods.ToastLong(context, "${response.body()?.msg}")
                                }
                            } else {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")
                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
                        }
    //                    UtilMethods.hideLoading()
                    }
                }catch (e: Throwable) {
                    runOnUiThread {
                        UtilMethods.ToastLong(context,"Server or Internet error : ${e.message}")
                    }
                    Log.e("TAG","Throwable : $e")
                    //UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gameInCondition(grStatus: model.gamerequeststatus.Result){
        val player = grStatus.playersOnline.toInt()
        val ticket = grStatus.totalTickets.toInt()
        when {
            player == 6 -> {
                // Max player 6 then start game
                callGameInApiFromCurrentUser(CURRENT_USER)
            }
            ticket >= 6 -> {
                // if tickets greater than 6 and wait 60 secs completed then start game
                callGameInApiFromCurrentUser(CURRENT_USER)
            }
            ticket < 6 && player < 6 -> {
                // if tickets below 6 and players below 6 and time left 60 sec then please wait extra 30 secs with message
                // 30 seconds
//                val minute:Long = 1000 * 30 // 1000 milliseconds = 1 second
//                // Count down interval 1 second
//                val countDownInterval: Long = 1000
//
//                timer(minute,countDownInterval).start()
            }
//            ticket >= 6 && player < 6 -> {
//                // if above conditions not fulfill and also elapsed 90 sec then show the "Opps something went wrong"
//                UtilMethods.ToastLong(baseContext,"Oops something went wrong. Please request again")
//                finish()
//            }

        }
    }
    private fun countDown(timer: Long){
        // to start CountDownTimer "time in millisecond"

        // to start CountDownTimer "time in millisecond"
        mCountDownTimer.start(timer)
        // on time finish
        mCountDownTimer.setOnTimeFinish(OnTimeFinish {
            //Toast.makeText(applicationContext,"finish",Toast.LENGTH_SHORT).show()
        })

        // on (success, failed) animation finish

        // on (success, failed) animation finish
        mCountDownTimer.setOnEndAnimationFinish(OnTimeFinish {
            // ---
        })
    }
    // Method to configure and return an instance of CountDownTimer object
    private fun timer(millisInFuture:Long,countDownInterval:Long): CountDownTimer{
        return object: CountDownTimer(millisInFuture,countDownInterval){

            override fun onTick(millisUntilFinished: Long){
                val timeRemaining = timeString(millisUntilFinished)
                tvTimer.text = timeRemaining
                if (timeout){
                    tvMsg.text = "Give you $timeRemaining sec more time to join"
                }
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onFinish() {
                tvTimer.text = ""
                if (grStatus != null) {
                    if (grStatus!!.totalTickets < "6" && grStatus!!.playersOnline < "6") {
                        if (timeout) {
                            tvMsg.text = "Oops something went wrong"
                            finish()
                        } else {
                            timeout = true
                            //tvMsg.text = "Give you 30 sec more time to join"
                            // TODO:("Get fix user details")
                            callFixUserGameRequestApi()
                            callTimers(30)
                        }
                    } else {
                        callGameRequestStatusApiFromCurrentUser(CURRENT_USER)
                    }
                }
            }
        }
    }
    // Method to get days hours minutes seconds from milliseconds
    private fun timeString(millisUntilFinished:Long):String{
        var millisUntilFinished:Long = millisUntilFinished
        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
        millisUntilFinished -= TimeUnit.DAYS.toMillis(days)

        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

        // Format the string
        return String.format(Locale.getDefault(),"%02d",seconds)
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callFixUserGameRequestApi(){
        val jsonFileString = Constants.getJsonDataFromAsset(applicationContext, "fixUser.json")
        Log.i("data", jsonFileString)

        val gson = Gson()
        val fixUser = object : TypeToken<ResFixUser>() {}.type

        var fixUserResponse: ResFixUser = gson.fromJson(jsonFileString, fixUser)

        Log.i("data", "> Item $fixUserResponse")
        if (fixUserResponse.fixUsers != null) {
            val context = this@LoadingActivity
            for (elements in fixUserResponse.fixUsers) {
                gameRequestApi(context,elements.userId,elements.sessionId,elements.amt.toString(),elements.ticketReq, keyModel.gameType.toString(),elements.tournamentId)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gameRequestApi(context: Context, userid: String, sesid: String
                               , amt: String, req_ticket: Int
                               , game_type: String, tournament_id: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            //UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = service.gameRequest(userid,sesid,amt,req_ticket.toString(),game_type,tournament_id)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.code() == 201) {
                                    //UtilMethods.ToastLong(context, "${response.body()?.msg}")
                                    val gameReqId = response.body()?.result?.get(0)?.requestID!!
                                    callGameRequestStatusApi(context,userid,sesid,amt,game_type,tournament_id,gameReqId, FIX_USER)
                                } else {
                                    UtilMethods.ToastLong(context, "${response.body()?.msg}")
                                }
                            } else {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                            }
                        } catch (e: HttpException) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")
                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(context,"Ooops: Something else went wrong : " + e.message)
                        }
                        //UtilMethods.hideLoading()
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
    override fun onStop() {
        super.onStop()
        Thread.interrupted()
        finish()
    }
    override fun onDestroy() {
        super.onDestroy()
        Thread.interrupted()
        finish()
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK ->  {
                // do something here
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }

    companion object {
        const val TAG = "LoadingScreen"
        const val KEY_GAME_IN_FIX_USER = "key_game_in_fix_user"
        const val CURRENT_USER = 0
        const val FIX_USER = 1
    }
}
