package com.newitzone.tambola

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import com.newitzone.tambola.utils.SharedPrefManager
import com.newitzone.tambola.utils.UtilMethods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit.TambolaApiService
import retrofit2.HttpException

class LoadingActivity : AppCompatActivity() {
    private var context: Context? = null
    internal val DELAY_MS: Long = 4000  //delay in milliseconds before task is to be executed
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

        // Api
        if (SharedPrefManager.getInstance(context as LoadingActivity).isLoggedIn){
            loginApi(
                context as LoadingActivity
                ,SharedPrefManager.getInstance(context as LoadingActivity).result.email_id
                ,SharedPrefManager.getInstance(context as LoadingActivity).passKey.toString()
                ,SharedPrefManager.getInstance(context as LoadingActivity).result.user_type
                ,SharedPrefManager.getInstance(context as LoadingActivity).result.login_st
                ,SharedPrefManager.getInstance(context as LoadingActivity).result.sid
                ,SharedPrefManager.getInstance(context as LoadingActivity).result.id)
        }else{
            // handler
            Handler().postDelayed({
                // TODO Auto-generated method stub
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }, DELAY_MS)
        }

    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CheckResult")
    private fun loginApi(
        context: Context, userID: String, passKey: String
        , userType: String, loginType: Int, sesId: String, userId: String){
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
                                SharedPrefManager.getInstance(context).saveUser(
                                    it,passKey)
                            }
                            if (SharedPrefManager.getInstance(context).isLoggedIn) {

                                val intent = Intent(context, HomeActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                //intent.putExtra("tes",SharedPrefManager.getInstance(context).result)
                                startActivity(intent)
                                finish()
                            }else{
                                UtilMethods.ToastLong(context,"Your cannot login because your account is blocked")
                            }
                        } else {
                            UtilMethods.ToastLong(context,"Error: ${response.code()}"+"\nMsg:${response.body()?.msg}")
                        }
                    } catch (e: HttpException) {
                        UtilMethods.ToastLong(context,"Exception ${e.message}")

                    } catch (e: Throwable) {
                        UtilMethods.ToastLong(context,"Ooops: Something else went wrong : " + e.message)
                    }
                    UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
}
