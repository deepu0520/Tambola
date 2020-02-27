package com.newitzone.tambola

import model.login.ResLogin
import android.annotation.SuppressLint
import android.content.Context
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
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit.RetrofitClient
import retrofit.TambolaApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private var context: Context? = null
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

        if(email.isEmpty()){
            text_input_user_id.error = "Email required"
            text_input_user_id.requestFocus()
        }


        if(password.isEmpty()){
            text_input_user_passkey.error = "Password required"
            text_input_user_passkey.requestFocus()
        }

        loginApiCall(email,password)
//        val intent = Intent(context, HomeActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        startActivity(intent)
//        finish()
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
    private fun loginApi(userID: String, userPassword: String){
        val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getlogin(userID,userPassword,0,0,"","")
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
                    toast("Ooops: Something else went wrong")
                }
            }
        }
    }
    private fun loginApiCall(userID: String, userPassword: String){
        RetrofitClient.instance.login(userID, userPassword,0,0,"","")
            .enqueue(object: Callback<ResLogin>{
                override fun onFailure(call: Call<ResLogin>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ResLogin>, response: Response<ResLogin>) {
                    if (response.code() == 200){
//                        SharedPrefManager.getInstance(applicationContext).saveUser(response.body()?.user!!)
//
//                        val intent = Intent(applicationContext, ProfileActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//
//                        startActivity(intent)

                        Toast.makeText(applicationContext, response.body()?.msg, Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(applicationContext, response.body()?.msg, Toast.LENGTH_LONG).show()
                    }

                }
            })
//        if(UtilMethods.isConnectedToInternet(context)){
//            UtilMethods.showLoading(context)
//            val observable = TambolaApiService.TambolaApiService().doLogin(LoginPostData(userID, userPassword))
//            observable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({ loginResponse ->
//                    UtilMethods.hideLoading()
//
//                    /** loginResponse is response data class*/
//
//                }, { error ->
//                    UtilMethods.hideLoading()
//                    UtilMethods.showLongToast(context, error.message.toString())
//                }
//                )
//        }else{
//            UtilMethods.showLongToast(context, "No Internet Connection!")
//        }
    }
}
