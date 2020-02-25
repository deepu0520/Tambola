package com.newitzone.tambola

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import butterknife.BindView
import butterknife.ButterKnife
import com.newitzone.tambola.utils.UtilMethods
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit.TambolaApiService

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

    fun onLogin(view: View){

        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
    fun onRegister(view: View){
//        val repository = SearchRepositoryProvider.provideSearchRepository()
//        repository.searchUsers("Lagos", "Java")
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//            .subscribe ({
//                    result ->
//                Log.d("Result", "There are ${result.items.size} Java developers in Lagos")
//            }, { error ->
//                error.printStackTrace()
//            })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CheckResult")
    private fun loginApiCall(userID: String, userPassword: String){
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
