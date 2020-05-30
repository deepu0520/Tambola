package com.newitzone.desitambola

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.RequiresApi
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.newitzone.desitambola.dialog.MessageDialog
import com.newitzone.desitambola.utils.Constants
import com.newitzone.desitambola.utils.KCustomToast
import com.newitzone.desitambola.utils.UtilMethods
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit.TambolaApiService

class RegistrationActivity : AppCompatActivity() {
    private var context: Context? = null
    private var userType: Int = 0
    private var loginType: Int = 0;
    private var sesId: String = "";
    private var userId: String = "";

    // text view
    @BindView(R.id.text_login_from_reg) lateinit var tvLogin: TextView
    @BindView(R.id.text_register_for_free) lateinit var btnRegister: TextView
    @BindView(R.id.text_terms_condition) lateinit var txtTnC: TextView
    // check box
    @BindView(R.id.checkBox_terms_condition) lateinit var cbTnC: CheckBox
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
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()
        this.context = this@RegistrationActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        // register from login screen
        btnRegister.setOnClickListener { view ->
            onRegister(view)
        }
        txtTnC.setOnClickListener {
            MessageDialog(context as RegistrationActivity, "Terms & Conditions", Constants.TERMS_CONDITIONS).show()
        }
        // terms and condition
        //txtTnC.text(R.string.txt_terms_condition)
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

        text_input_fname.error = null
        text_input_email.error = null
        text_input_password.error = null
        text_input_confirm_password.error = null


        var cancel = false
        var focusable: View? = null

        if(fName.isEmpty()){
            text_input_fname.error = "First name cannot be blank"
            focusable = text_input_fname
            cancel = true
        }
        if(email.isEmpty()){
            text_input_email.error = "Email cannot be blank"
            focusable = text_input_email
            cancel = true
        }
        if(password.isEmpty()){
            text_input_password.error = "Password cannot be blank"
            focusable = text_input_password
            cancel = true
        }
        if(confirmPassword.isEmpty()){
            text_input_confirm_password.error = "Confirm password cannot be blank"
            focusable = text_input_confirm_password
            cancel = true
        }
        if(password != confirmPassword){
            text_input_confirm_password.error = "Confirm password is not match"
            focusable = text_input_confirm_password
            cancel = true
        }
        if(!cbTnC.isChecked){
            Snackbar.make(checkBox_terms_condition, "Please accept terms and condition", Snackbar.LENGTH_LONG).show()
            focusable = checkBox_terms_condition
            cancel = true
        }

        if (cancel){
            focusable!!.requestFocus()
        }else {
            // TODO: To login
            // this block is only called if form is valid.
            // do something with a valid form state.
            val context = this@RegistrationActivity
            registerApi(context,fName,lName,email,mobile,password,dob,userType.toString(),img,img)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("CheckResult")
    private fun registerApi(context: Context,fname: String,lname: String
                         ,emailID: String,mobileNo: String,passkey: String,dob: String,userType: String,img: String,fbImg: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = service.registration(fname, lname, emailID, mobileNo, passkey, dob,userType,img,fbImg)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if(response.body()?.status == 1) {
                                    // save the user details
                                    //UtilMethods.ToastLong(context,"Congratulation you have earn 100 chips")
                                    KCustomToast.toastWithFont(context as Activity,"Congratulation you have earn 100 chips")
                                    //UtilMethods.ToastLong(context,"${response.body()?.msg}")
                                    val intent = Intent(context, LoginActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    startActivity(intent)
                                    finish()
                                }else {
                                    UtilMethods.ToastLong(context,"${response.body()?.msg}")
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
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK ->  {
                // do something here
                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
