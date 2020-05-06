package com.newitzone.desitambola

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.github.anastr.flattimelib.CountDownTimerView
import com.github.anastr.flattimelib.intf.OnTimeFinish
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.newitzone.desitambola.utils.Constants
import com.newitzone.desitambola.utils.UtilMethods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.login.Result
import model.tournament.Tournament
import model.tournament.start.ResTournamentStart
import retrofit.TambolaApiService
import java.util.*
import java.util.concurrent.TimeUnit


class LoadingTournamentGameActivity : AppCompatActivity() {
    private var context: Context? = null
    private val DELAY_MS: Long = 4000  //delay in milliseconds before task is to be executed
    private var i = 0
    private lateinit var login: Result
    private lateinit var tournament: Tournament
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
        this.context = this@LoadingTournamentGameActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        login = intent.getSerializableExtra(HomeActivity.KEY_LOGIN) as Result
        tournament = intent.getSerializableExtra(HomeActivity.KEY_TOURNAMENT) as Tournament
        if (login != null){// && tournament != null){
            tvMsg.text = "Loading..."
            callTimers(60)  // call for 60 sec

//            // TODO for testing
//            val intent = Intent(context, PlayTournamentGameActivity::class.java)
//            intent.putExtra(HomeActivity.KEY_LOGIN, login)
//            intent.putExtra(KEY_TOURNAMENT_START, getTournamentStartJson())
//            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//            startActivity(intent)
//            finish()
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
    private fun tournamentStartApi(context: Context, userid: String, sesid: String, tournament_id: String, gameId: String) {
        if (UtilMethods.isConnectedToInternet(context)) {
            //UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = service.tournamentStart(userid, sesid, tournament_id, gameId)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body()?.status == 1) {
                                    val tournamentStart = response.body()
                                    if (tournamentStart != null) {

                                        // TODO: stop timer
                                        countDownTimer.cancel()
                                        // TODO Auto-generated method stub
                                        val intent = Intent(context, PlayTournamentGameActivity::class.java)
                                        intent.putExtra(HomeActivity.KEY_LOGIN, login)
                                        intent.putExtra(KEY_TOURNAMENT_START,tournamentStart)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                        startActivity(intent)
                                        finish()
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
                    UtilMethods.ToastLong(context,"Server or Internet error : ${e.message}")
                    Log.e("TAG","Throwable : $e")
                    //UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
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
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onFinish() {
                tvTimer.text = "Tournament started..."
                val context = this@LoadingTournamentGameActivity
                if (login != null && tournament != null) {
                    tournamentStartApi(
                        context,
                        login.id,
                        login.sid,
                        tournament.id,
                        tournament.game_id
                    )
                }else{
                    UtilMethods.ToastLong(context,"Something went wrong")
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

    // TODO: Tournament Start Json by local
    private fun getTournamentStartJson(): ResTournamentStart{
        val jsonFileString = Constants.getJsonDataFromAsset(applicationContext, "tournamentStart.json")
        Log.i("data", jsonFileString)

        val gson = Gson()
        val tournamentStart = object : TypeToken<ResTournamentStart>() {}.type

        return gson.fromJson(jsonFileString, tournamentStart)
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
        const val TAG = "LoadingTournamentGameScreen"
        const val KEY_TOURNAMENT_START = "key_tournament_start"
    }
}
