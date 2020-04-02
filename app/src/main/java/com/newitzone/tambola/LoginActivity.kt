package com.newitzone.tambola

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.vvalidator.form
import com.google.android.material.textfield.TextInputLayout
import com.newitzone.tambola.utils.SharedPrefManager
import com.newitzone.tambola.utils.UtilMethods
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit.TambolaApiService
import retrofit2.HttpException
import java.io.Serializable

class LoginActivity : AppCompatActivity() {
    private var context: Context? = null
    private var userType: Int = 0
    private var loginType: Int = 0;
    private var sesId: String = "";
    private var userId: String = "";

    // text view
    @BindView(R.id.text_login) lateinit var btnLogin: TextView
    @BindView(R.id.text_register) lateinit var tvRegister: TextView
    // TextInputLayout login
    @BindView(R.id.text_input_user_id) lateinit var tInputUserId: TextInputLayout
    @BindView(R.id.text_input_user_passkey) lateinit var tInputUserPassKey: TextInputLayout

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        this.context = this@LoginActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        // login
        btnLogin.setOnClickListener { view ->
            onLogin(view)
        }
        // register from login screen
        tvRegister.setOnClickListener { view ->
            val intent = Intent(context, RegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onLogin(view: View){
        val email = text_input_user_id.editText!!.text.toString().trim()
        val password = text_input_user_passkey.editText!!.text.toString().trim()

        form {
            input(R.id.input_user_id , name = "Username(Email)") {
                isNotEmpty()
                isEmail()
            }
            input(R.id.input_user_passkey , name = "Pass Key") {
                isNotEmpty()
            }

            submitWith(R.id.text_login) {
                // this block is only called if form is valid.
                // do something with a valid form state.
                val  context = this@LoginActivity
                loginApi(
                    context,
                    email,
                    password,
                    userType.toString(),
                    loginType.toString(),
                    sesId,
                    userId
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CheckResult")
    private fun loginApi(context: Context,userID: String,passKey: String
                         ,userType: String,loginType: String,sesId: String,userId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
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
                                            , SharedPrefManager.getInstance(context).result.emailId
                                            , SharedPrefManager.getInstance(context).passKey.toString()
                                            , SharedPrefManager.getInstance(context).result.userType
                                            , "1"
                                            , SharedPrefManager.getInstance(context).result.sid
                                            , SharedPrefManager.getInstance(context).result.id
                                        )
                                    }
                                } else {
                                    UtilMethods.ToastLong(context,"Your cannot login because your account is blocked")
                                }
                            } else {
                                UtilMethods.ToastLong(context, "Error: ${response.code()}" + "\nMsg:${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")
                        }
                        UtilMethods.hideLoading()
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
