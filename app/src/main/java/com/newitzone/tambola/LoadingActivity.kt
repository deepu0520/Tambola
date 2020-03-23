package com.newitzone.tambola

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import model.gamein.GameIn
import model.gamein.Result
import retrofit.TambolaApiService
import retrofit2.HttpException

class LoadingActivity : AppCompatActivity() {
    private var context: Context? = null
    private val DELAY_MS: Long = 4000  //delay in milliseconds before task is to be executed
    private lateinit var keyModel: KeyModel
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_loading)
        supportActionBar?.hide()
        this.context = this@LoadingActivity
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        keyModel = intent.getSerializableExtra(HomeActivity.KEY_MODEL) as KeyModel
        if (keyModel != null){
            callApi()
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callApi(){
        val context= this@LoadingActivity
        val userId = SharedPrefManager.getInstance(context).result.id
        val sessionId = SharedPrefManager.getInstance(context).result.sid
        if (sessionId.isNotBlank()) {
            //TODO: Use this
            gameRequestStatusApi(
                context,
                userId,
                sessionId,
                keyModel.amount.toString(),
                keyModel.gameType.toString(),
                keyModel.tournamentId
            )
        }else{
            UtilMethods.ToastLong(context,"Session expired")
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callGameInApi(){
        val context= this@LoadingActivity
        val userId = SharedPrefManager.getInstance(context).result.id
        val sessionId = SharedPrefManager.getInstance(context).result.sid
        if (sessionId.isNotBlank()) {
            //TODO: Use this
            gameInApi(
                context,
                userId,
                sessionId,
                keyModel.amount.toString(),
                keyModel.gameType.toString(),
                keyModel.tournamentId
            )
        }else{
            UtilMethods.ToastLong(context,"Session expired")
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gameRequestStatusApi(context: Context, userid: String, sesid: String
                                     , amt: String, game_type: String, tournament_id: String) {
        if (UtilMethods.isConnectedToInternet(context)) {
            //UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.gameRequestStatus(userid, sesid, game_type, amt, tournament_id)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.code() == 200) {
                                    callGameInApi()
                                    //getGameInfromJson()
                                } else {
                                    UtilMethods.ToastLong(context, "${response.body()?.msg}")
                                }
                            } else {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")
                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(
                                context,
                                "Ooops: Something else went wrong : " + e.message
                            )
                        }
    //                    UtilMethods.hideLoading()
                    }
                }catch (e: Throwable) {
                    runOnUiThread {
                        UtilMethods.ToastLong(context,"Server or Internet error : ${e.message}")
                    }
                    Log.e("TAG","Throwable : $e")
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
    private fun getGameInfromJson(){
        val jsonFileString = Constants.getJsonDataFromAsset(applicationContext, "gameIn.json")
        Log.i("data", jsonFileString)

        val gson = Gson()
        val GameInType = object : TypeToken<GameIn>() {}.type

        var gameInResponse: GameIn = gson.fromJson(jsonFileString, GameInType)

        Log.i("data", "> Item $gameInResponse")
        val intent = Intent(context, PlayActivity::class.java)
        intent.putExtra(HomeActivity.KEY_MODEL, keyModel)
        intent.putExtra(HomeActivity.KEY_GAME_IN ,gameInResponse)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gameInApi(context: Context, userid: String, sesid: String
                                     , amt: String, game_type: String, tournament_id: String) {
        if (UtilMethods.isConnectedToInternet(context)) {
            //UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = service.gameInV2(userid, sesid, game_type, amt, tournament_id)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.code() == 200) {
                                    UtilMethods.ToastLong(context, "${response.body()?.msg}")
                                    val gameInResponse = response.body()//?.result?//.get(1)
                                    if (gameInResponse != null) {
                                        // handler
                                        //Handler().postDelayed({
                                        // TODO Auto-generated method stub
                                        val intent = Intent(context, PlayActivity::class.java)
                                        intent.putExtra(HomeActivity.KEY_MODEL, keyModel)
                                        intent.putExtra(HomeActivity.KEY_GAME_IN ,gameInResponse)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                        startActivity(intent)
                                        //}, DELAY_MS)
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
                    UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }

}
