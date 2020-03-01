package com.newitzone.tambola

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
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

class LoginActivity : AppCompatActivity() {
    private var context: Context? = null
    private var userType: Int = 0
    private var loginType: Int = 0;
    private var sesId: String = "";
    private var userId: String = "";

    // linear layout
    @BindView(R.id.linear_login) lateinit var lLLogin: LinearLayout
    @BindView(R.id.linear_register) lateinit var lLRegister: LinearLayout
    // text view
    @BindView(R.id.text_login) lateinit var btnLogin: TextView
    @BindView(R.id.text_login_from_reg) lateinit var tvLogin: TextView
    @BindView(R.id.text_register) lateinit var tvRegister: TextView
    @BindView(R.id.text_register_for_free) lateinit var btnRegister: TextView
    // TextInputLayout
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
        tvLogin.setOnClickListener { view ->
            lLLogin.visibility = View.VISIBLE
            lLRegister.visibility = View.GONE
        }
        // register from login screen
        tvRegister.setOnClickListener { view ->
            lLLogin.visibility = View.GONE
            lLRegister.visibility = View.VISIBLE
        }
        // register from login screen
        btnRegister.setOnClickListener { view ->
            onRegister(view)
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

            submitWith(R.id.text_login) { result ->
                // this block is only called if form is valid.
                // do something with a valid form state.
                context?.let { loginApi(it,email,password,userType,loginType,sesId,userId) }
            }
        }

        //updateProfileApi()
    }
    fun toast(str: String){
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show()
    }
    fun onRegister(view: View){
//        val repository = SearchRepositoryProvider.provideSearchRepository()
//        repository.searchUsers("Lagos", "Java")
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe ({
//                    result ->
//                Log.d("model.login.Result", "There are ${result.items.size} Java developers in Lagos")
//            }, { error ->
//                error.printStackTrace()
//            })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CheckResult")
    private fun loginApi(context: Context,userID: String,userPassword: String
                         ,userType: Int,loginType: Int,sesId: String,userId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.getlogin(userID, userPassword, userType, loginType, sesId, userId)
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            response.body()?.result?.let {
                                SharedPrefManager.getInstance(context).saveUser(
                                    it
                                )
                            }
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            //intent.putExtra("tes",SharedPrefManager.getInstance(context).result)
                            startActivity(intent)
                            finish()
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
    private fun updateProfileApi(){
        val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.updateProfile("Rakesh","Kumar","9716561086","abc@123","3C96A37F-5C3C-4849-A4F3-984B26D01FAD","6C87D629-0469-4CFC-8EC6-336A183B67F5","1989-07-13","")
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) {
                        toast("success: ${response.code()}")
                        //Do something with response e.g show to the UI.
                    } else {
                        toast("Error: ${response.code()}")
                    }
                } catch (e: HttpException) {
                    toast("Exception ${e.message}")
                } catch (e: Throwable) {
                    toast("Ooops: Something else went wrong : "+e.message)
                }
            }
        }
    }
}
