package com.newitzone.tambola

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.newitzone.tambola.utils.SharedPrefManager
import com.newitzone.tambola.utils.UtilMethods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit.TambolaApiService

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class MainActivity : AppCompatActivity() {
    private var context: Context? = null
    internal val DELAY_MS: Long = 5000  //delay in milliseconds before task is to be executed
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
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        //val flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        this.context = this@MainActivity
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        // Api
        if (SharedPrefManager.getInstance(context as MainActivity).isLoggedIn){
            loginApi(
                context as MainActivity
                , SharedPrefManager.getInstance(context as MainActivity).result.emailId
                , SharedPrefManager.getInstance(context as MainActivity).passKey.toString()
                , SharedPrefManager.getInstance(context as MainActivity).result.userType
                , SharedPrefManager.getInstance(context as MainActivity).result.loginSt
                , SharedPrefManager.getInstance(context as MainActivity).result.sid
                , SharedPrefManager.getInstance(context as MainActivity).result.id)
        }else {
            // handler
            Handler().postDelayed({
                // TODO Auto-generated method stub
                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }, DELAY_MS)
        }
    }

    fun FullScreencall() {
        if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            val decorView = window.decorView
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CheckResult")
    private fun loginApi(
        context: Context, userID: String, passKey: String
        , userType: String, loginType: String, sesId: String, userId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.getlogin(userID, passKey, userType, loginType, sesId, userId)
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            // save the user details
                            response.body()?.result?.let {
                                SharedPrefManager.getInstance(context).saveUser(it,passKey)
                            }

                            if (SharedPrefManager.getInstance(context).isLoggedIn) {
                                if(loginType == "1") {
                                    val intent = Intent(context, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    intent.putExtra(HomeActivity.KEY_LOGIN,SharedPrefManager.getInstance(context).result)
                                    startActivity(intent)
                                    finish()
                                }else {
                                    loginApi(context
                                        , SharedPrefManager.getInstance(context as MainActivity).result.emailId
                                        , SharedPrefManager.getInstance(context as MainActivity).passKey.toString()
                                        , SharedPrefManager.getInstance(context as MainActivity).result.userType
                                        , "1"
                                        , SharedPrefManager.getInstance(context as MainActivity).result.sid
                                        , SharedPrefManager.getInstance(context as MainActivity).result.id
                                    )
                                }
                            }else{
                                UtilMethods.ToastLong(context,"Your cannot login because your account is blocked")
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
}
