package com.newitzone.tambola

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
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

    // linear layout
    @BindView(R.id.linear_login) lateinit var lLLogin: LinearLayout
    @BindView(R.id.linear_register) lateinit var lLRegister: LinearLayout
    // text view
    @BindView(R.id.text_login) lateinit var btnLogin: TextView
    @BindView(R.id.text_login_from_reg) lateinit var tvLogin: TextView
    @BindView(R.id.text_register) lateinit var tvRegister: TextView
    @BindView(R.id.text_register_for_free) lateinit var btnRegister: TextView
    @BindView(R.id.text_terms_condition) lateinit var txtTnC: TextView
    // check box
    @BindView(R.id.checkBox_terms_condition) lateinit var cbTnC: CheckBox
    // TextInputLayout login
    @BindView(R.id.text_input_user_id) lateinit var tInputUserId: TextInputLayout
    @BindView(R.id.text_input_user_passkey) lateinit var tInputUserPassKey: TextInputLayout
//    // TextInputLayout registration
    @BindView(R.id.text_input_fname) lateinit var tInputfName: TextInputLayout
    @BindView(R.id.text_input_lname) lateinit var tInputlName: TextInputLayout
    @BindView(R.id.text_input_email) lateinit var tInputlEmail: TextInputLayout
    @BindView(R.id.text_input_mobile) lateinit var tInputlMobile: TextInputLayout
    @BindView(R.id.text_input_password) lateinit var tInputlPassword: TextInputLayout
    @BindView(R.id.text_input_confirm_password) lateinit var tInputlConfirmPassword: TextInputLayout

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
        // terms and condition
        //txtTnC.text(R.string.txt_terms_condition)
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
                val  context = this@LoginActivity
                if (SharedPrefManager.getInstance(context).userId.isNullOrBlank()) {
                        loginApi(
                            context,
                            email,
                            password,
                            userType.toString(),
                            loginType.toString(),
                            sesId,
                            userId
                        )
                }else{
                    loginType = 1
                    userId = SharedPrefManager.getInstance(context).userId.toString()
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

        //updateProfileApi()
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onRegister(view: View){
        val fName = text_input_fname.editText!!.text.toString().trim()
        val lName = text_input_lname.editText!!.text.toString().trim()
        val email = text_input_email.editText!!.text.toString().trim()
        val mobile = text_input_mobile.editText!!.text.toString().trim()
        val dob = ""
        val password = text_input_password.editText!!.text.toString().trim()
        val confirmPassword = text_input_confirm_password.editText!!.text.toString().trim()
        val img = ""
        form {
            inputLayout(R.id.text_input_fname , name = "First Name") {
                isNotEmpty()
            }
            inputLayout(R.id.text_input_lname , name = "Last Name") {
                isNotEmpty()
            }
            inputLayout(R.id.text_input_email , name = "Email(User name)") {
                isNotEmpty()
                isEmail()
            }
            inputLayout(R.id.text_input_password , name = "Password") {
                isNotEmpty()
            }
            inputLayout(R.id.text_input_confirm_password , name = "Confirm Password") {
                isNotEmpty()
            }
            checkable(R.id.checkBox_terms_condition , name = "Terms and Condition") {
                isChecked()
            }


            submitWith(R.id.text_register_for_free) { result ->
                // this block is only called if form is valid.
                if (password.compareTo(confirmPassword) == 0) {
                    // do something with a valid form state.
                    context?.let {
                        registerApi(
                            it,
                            fName,
                            lName,
                            email,
                            mobile,
                            password,
                            dob,
                            userType.toString(),
                            img
                        )
                    }
                }else{
                    input(R.id.input_confirm_password , name = "Confirm Password") {
                        context?.let { UtilMethods.ToastLong(it,"Password is not match") }
                    }
                }
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
                                intent.putExtra(HomeActivity.KEY_LOGIN,SharedPrefManager.getInstance(context).result)
                                startActivity(intent)
                                finish()
                            }else{
                                UtilMethods.ToastLong(context,"Your cannot login because your account is blocked")
                            }
                        } else {
                            UtilMethods.ToastLong(context,"Error: ${response.code()}"+"\nMsg:${response.body()?.msg}")
                        }
                    } catch (e: Exception) {
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
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CheckResult")
    private fun registerApi(context: Context,fname: String,lname: String
                         ,emailID: String,mobileNo: String,passkey: String,dob: String,userType: String,img: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.registration(fname, lname, emailID, mobileNo, passkey, dob,userType,img)
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            // save the user details
                            UtilMethods.ToastLong(context,"${response.body()?.msg}")
                            lLLogin.visibility = View.VISIBLE
                            lLRegister.visibility = View.GONE
                        } else {
                            UtilMethods.ToastLong(context,"Error: ${response.code()}"+"\nMsg:${response.body()?.msg}")
                        }
                    } catch (e: Exception) {
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
