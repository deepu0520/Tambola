package com.newitzone.desitambola

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.newitzone.desitambola.databinding.ActivityMainBinding
import com.newitzone.desitambola.utils.Constants
import com.newitzone.desitambola.utils.DesiTambolaPreferences
import com.newitzone.desitambola.utils.UtilMethods
//import kotlinx.android.synthetic.main.layout_loading_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.login.ResLogin
import model.login.Result
import model.tournament.ResTournament
import retrofit.TambolaApiService
import java.io.IOException
import java.util.*

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class MainActivity : AppCompatActivity(), InstallStateUpdatedListener,TextToSpeech.OnInitListener{
    private lateinit var appUpdateManager: AppUpdateManager

    private var tts: TextToSpeech? = null
    private var context: Context? = null
    private val DELAY_MS: Long = 3000
    private lateinit var binding: ActivityMainBinding
    //delay in milliseconds before task is to be executed
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
       // setContentView(R.layout.activity_main)
        setContentView(binding.root)
        supportActionBar?.hide()
        //val flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        this.context = this@MainActivity
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        //tts = TextToSpeech(this, this)
        startSound("sounds/desi_tambola.mp3")

        // In-App Update
        // Creates instance of the manager.
        appUpdateManager = AppUpdateManagerFactory.create(context)

        // Returns an intent object that you use to check for an update.
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // For a flexible update, use AppUpdateType.FLEXIBLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                // Request the update.
                requestUpdate(appUpdateInfo)
            }else {
                // call the Method
                // handler
                Handler().postDelayed({
                    val context = this@MainActivity
                    // Api
                    val mLogin = DesiTambolaPreferences.getLogin(context)  //getLoginJson().result[0]
                    val passKey = DesiTambolaPreferences.getPassKey(context)
                    if(mLogin.isLogin) {
                        loginApi(context, mLogin.emailId, passKey, mLogin.userType, mLogin.loginSt, mLogin.sid, mLogin.id)
                    }else {
                        // TODO Auto-generated method stub
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                }, DELAY_MS)
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun loginApi(
        context: Context, userID: String, passKey: String
        , userType: String, loginType: String, sesId: String, userId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.getlogin(userID, passKey, userType, loginType, sesId, userId,"")
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body()?.status == 1) {
                                if (loginType == LoginActivity.LOGIN_TYPE_LOG) {
                                    // Save preferences
                                    response.body()?.result?.get(0)!!.userType = userType
                                    DesiTambolaPreferences.setLogin(context, response.body()?.result?.get(0))
                                    // redirect to home screen
                                    val intent = Intent(context, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    intent.putExtra(HomeActivity.KEY_LOGIN, response.body()?.result?.get(0))
                                    startActivity(intent)
                                    finish()
                                } else {
                                    loginApi(context,userID,passKey,userType,LoginActivity.LOGIN_TYPE_LOG, sesId, userId)
                                }
                            } else {
                                // TODO: Goto Login screen
                                val intent = Intent(context, LoginActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            UtilMethods.ToastLong(context,"Error: ${response.code()}"+"\nMsg:${response.body()?.msg}")
                        }

                    } catch (e: Exception) {
                        UtilMethods.ToastLong(context,"Exception ${e.message}")

                    } catch (e: Throwable) {
                        UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
                    }
                    UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
    private fun startSound(filename: String) {
        var afd: AssetFileDescriptor? = null
        try {
            afd = resources.assets.openFd(filename)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val player = MediaPlayer()
        try {
            assert(afd != null)
            player.setDataSource(afd!!.fileDescriptor, afd.startOffset, afd.length)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            player.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        player.start()
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onInit(status: Int) {

        if (status == TextToSpeech.SUCCESS) {
            // set US English as language for tts
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS","The Language specified is not supported!")
            } else {
                //buttonSpeak!!.isEnabled = true
                speakOut(getString(R.string.speak_app_name))
            }

        } else {
            Log.e("TTS", "Initilization Failed!")
        }

    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo?) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE, //  HERE specify the type of update flow you want
            this,   //  the instance of an activity
            REQUEST_CODE_IMMEDIATE_UPDATE
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMMEDIATE_UPDATE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    //  handle user's approval
                    //Snackbar.make(txt_message, R.string.user_approval, Snackbar.LENGTH_LONG).show()
                    Snackbar.make(binding.root, R.string.user_approval, Snackbar.LENGTH_LONG).show()
                   // Toast.makeText(this@MainActivity, R.string.user_approval, Toast.LENGTH_SHORT).show()
                }
                Activity.RESULT_CANCELED -> {
                    //  handle user's rejection
                    // Snackbar.make(txt_message, R.string.user_cancel, Snackbar.LENGTH_LONG).show()
                    Snackbar.make(binding.root, R.string.user_cancel, Snackbar.LENGTH_LONG).show()

                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    //  handle update failure
                  //  Snackbar.make(txt_message, R.string.app_update_failed, Snackbar.LENGTH_LONG).show()
                    Snackbar.make(binding.root, R.string.app_update_failed, Snackbar.LENGTH_LONG).show()

                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        appUpdateManager.appUpdateInfo.addOnSuccessListener {
            if (it.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                requestUpdate(it)
            }
        }
    }
    override fun onDestroy() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
        appUpdateManager.unregisterListener(this)
    }
    override fun onStateUpdate(state: InstallState?) {
        //TODO: ("Not yet implemented")
        if (state != null) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                notifyUser()
            }
        }
    }
    private fun notifyUser() {
        Snackbar.make(binding.root, R.string.restart_to_update, Snackbar.LENGTH_INDEFINITE)

            .setAction(R.string.action_restart) {
                appUpdateManager.completeUpdate()
                appUpdateManager.unregisterListener(this)
            }
            .show()
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null,"")
    }

    // TODO: Tournament Start Json by local
    private fun getLoginJson(): ResLogin {
        val jsonFileString = context?.let { Constants.getJsonDataFromAsset(it, "login.json") }
        Log.i("data", jsonFileString!!)

        val gson = Gson()
        val resLogin = object : TypeToken<ResLogin>() {}.type
        return gson.fromJson(jsonFileString, resLogin)
    }
    override fun onStop() {
        // Shutdown TTS
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onStop()
    }


    companion object {
        private const val REQUEST_CODE_IMMEDIATE_UPDATE = 9999
    }

}
