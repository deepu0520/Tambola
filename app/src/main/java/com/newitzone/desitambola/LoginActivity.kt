package com.newitzone.desitambola

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.material.textfield.TextInputLayout
import com.newitzone.desitambola.dialog.MessageDialog
import com.newitzone.desitambola.utils.DesiTambolaPreferences
import com.newitzone.desitambola.utils.KCustomToast
import com.newitzone.desitambola.utils.UtilMethods
import database.DatabaseClient
import database.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.login.Result
import org.json.JSONObject
import retrofit.TambolaApiService
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginActivity : AppCompatActivity() {
    private var context: Context? = null
    private var sesId: String = "";
    private var userId: String = "";
    private lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"

    // text view
    @BindView(R.id.text_login) lateinit var btnLogin: TextView
    @BindView(R.id.login_button) lateinit var btnFacebookLogin: LoginButton
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
        // facebook
        FacebookSdk.sdkInitialize(applicationContext);
        disconnectFromFacebook()
        btnFacebookLogin.setReadPermissions(listOf(EMAIL));
        btnFacebookLogin.setOnClickListener {
            onFacebookLogin()
        }
        // login
        btnLogin.setOnClickListener { view ->
            onLogin(view)
        }
        // register from login screen
        tvRegister.setOnClickListener {
            val intent = Intent(context, RegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun onFacebookLogin(){
        // TODO: print hashkey
        context?.let { printHashKey(it) }

        // Facebook
        callbackManager = CallbackManager.Factory.create()
        //LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onSuccess(loginResult: LoginResult?) {
                    // App code
                    val accessToken: AccessToken = AccessToken.getCurrentAccessToken()
                    val isLoggedIn = accessToken != null && !accessToken.isExpired
                    if(isLoggedIn){
                        if (loginResult != null) {
                            getFacebookUserDetails(loginResult)
                        }
                    }
                    //Log.i(TAG, "Facebook result: "+loginResult.toString())
                }

                override fun onCancel() {
                    // App code
                    Log.i(TAG, "Facebook cancel")
                }

                override fun onError(exception: FacebookException?) {
                    // App code
                    Log.i(TAG, "Facebook exception: "+exception.toString())
                }
            })
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getFacebookUserDetails(loginResult: LoginResult) {
        val dataRequest = GraphRequest.newMeRequest(loginResult.accessToken) { json_object, response ->
            try {
                val id = if (json_object.optString("id") == null) "" else json_object.optString("id")
                val name = if (json_object.optString("name") == null) "" else json_object.optString("name")
                val email = if (json_object.optString("email") == null) "" else json_object.optString("email")
                val gender = if (json_object.optString("gender") == null) "" else json_object.optString("gender")
                val birthday = if (json_object.optString("birthday") == null) "" else json_object.optString("birthday")
                val jsPic = JSONObject(json_object.getString("picture"))
                val jsData = JSONObject(jsPic.getString("data"))
                val picture = if (jsData.optString("url") == null) "" else jsData.optString("url")
                Log.i("Response", "fb_details:$json_object")
                if (email.isNotEmpty()) {
                    //TODO: Login Request
                    val  context = this@LoginActivity
                    loginApi(context, email,"",USER_TYPE_FACEBOOK, LOGIN_TYPE_INFO, sesId, userId , name, birthday, "", picture)
                } else {
                    Toast.makeText(context,"Email id not found in your facebook account",Toast.LENGTH_LONG).show()
                }
            } catch (e: java.lang.Exception) {
                Toast.makeText(context, "Exception: $e", Toast.LENGTH_LONG).show()
            }
        }
        val permissionParam = Bundle()
        permissionParam.putString("fields","id,name,email,gender,birthday,picture.width(320).height(320)")
        dataRequest.parameters = permissionParam
        dataRequest.executeAsync()
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun onLogin(view: View){
        val email = text_input_user_id.editText!!.text.toString().trim()
        val password = text_input_user_passkey.editText!!.text.toString().trim()
        var cancel = false
        var focusable: View? = null

        if(email.isEmpty()){
            text_input_user_id.error = "Email cannot be blank"
            focusable = text_input_user_id
            cancel = true
        }
        if(password.isEmpty()){
            text_input_user_id.error = "Password cannot be blank"
            focusable = text_input_user_id
            cancel = true
        }

        if (cancel){
            focusable!!.requestFocus()
        }else {
            // TODO: To login
            // this block is only called if form is valid.
            // do something with a valid form state.
            val  context = this@LoginActivity
            loginApi(
                context,
                email,
                password,
                USER_TYPE_NORMAL,
                LOGIN_TYPE_INFO,
                sesId,
                userId,
                "",
                "",
                "",
                ""
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun loginApi(context: Context,userID: String,passKey: String
                         ,userType: String,loginType: String,sesId: String, userId: String, name: String, birthday: String, img: String, fbImg: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.getlogin(userID, passKey, userType, loginType, sesId, userId, fbImg)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body()?.status == 1) {

                                    if (loginType == LOGIN_TYPE_LOG) {
                                        response.body()?.result?.get(0)!!.userType = userType
                                        // Save preferences
                                        DesiTambolaPreferences.setLogin(context, response.body()?.result?.get(0))
                                        DesiTambolaPreferences.setPassKey(context, passKey)
                                        // Login status check
                                        if(DesiTambolaPreferences.getLogin(context).isLogin) {
                                            // hide progress dialog
                                            UtilMethods.hideLoading()
                                            // go to home screen
                                            val intent = Intent(context, HomeActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                            intent.putExtra(HomeActivity.KEY_LOGIN,response.body()?.result?.get(0))
                                            startActivity(intent)
                                            finish()
                                        }
                                    } else {
                                        UtilMethods.hideLoading()
                                        loginApi(
                                            context,
                                            userID,
                                            passKey,
                                            userType,
                                            LOGIN_TYPE_LOG,
                                            response.body()?.result?.get(0)!!.sid,
                                            response.body()?.result?.get(0)!!.id,
                                            name,
                                            birthday,
                                            img,
                                            fbImg
                                        )
                                    }
                                }else if (response.body()?.status == 0) {
                                    if (userType == USER_TYPE_FACEBOOK) {
                                        UtilMethods.hideLoading()
                                        registerApi(context,name, "", userID, "", "", birthday, userType, img, fbImg)
                                    }else{
                                        MessageDialog(context as Activity, "",""+response.body()?.msg).show()
                                    }
                                }
                            } else {
                                UtilMethods.ToastLong(context, "Error: ${response.code()}" + "\nMsg:${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Exception: ${e.message}")
                            UtilMethods.ToastLong(context, "Exception: ${e.message}")
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
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun registerApi(context: Context, fname: String, lname: String
                            , emailID: String, mobileNo: String, passkey: String, dob: String, userType: String, img: String, fbImg: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try{
                    val response = service.registration(fname, lname, emailID, mobileNo, passkey, dob, userType,img, fbImg)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body()?.status == 1) {
                                    // save the user details
                                    //UtilMethods.ToastLong(context,"Congratulation you have earn 100 chips")
                                    KCustomToast.toastWithFont(
                                        context as Activity,
                                        "Congratulation you have earn 100 chips"
                                    )
                                    UtilMethods.hideLoading()
                                    loginApi(
                                        context,
                                        emailID,
                                        passkey,
                                        userType,
                                        "0",
                                        "",
                                        "",
                                        "$fname $lname",
                                        dob,
                                        img,
                                        fbImg
                                    )
                                } else {
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
    private fun disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return  // already logged out
        }
        GraphRequest(
            AccessToken.getCurrentAccessToken(),"/me/permissions/",null,HttpMethod.DELETE,
            GraphRequest.Callback {
                LoginManager.getInstance().logOut()
            }).executeAsync()
    }

    private fun printHashKey(pContext: Context) {
        try {
            val info: PackageInfo = pContext.packageManager.getPackageInfo(pContext.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i(TAG, "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "printHashKey()", e)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "printHashKey()", e)
        }
    }
    companion object{
        const val TAG = "LoginActivity"
        const val LOGIN_TYPE_INFO = "0"
        const val LOGIN_TYPE_LOG = "1"
        const val USER_TYPE_NORMAL = "0"
        const val USER_TYPE_FACEBOOK = "1"
        const val DATABASE_NAME = "DesiTambola"
        fun databaseExist(context: Context,dbName: String): Boolean {
            val dbFile: File = context.getDatabasePath(dbName)
            return dbFile.exists()
        }
        fun userSaveIntoDb(context: Context, login: Result, passKey: String){
            val user = User()
            user.userId = login.id
            user.inactive = login.inactive.toInt()
            user.fname = login.fname
            user.lname = login.lname
            user.dob = login.dob
            user.emailId = login.emailId
            user.code = login.code
            user.mobileNo = login.mobileNo
            user.userType = login.userType.toInt()
            user.img = login.img
            user.fbImg = login.fbImg
            user.enTime = login.enTime
            user.acBal = login.acBal.toDouble()
            user.acChipsBal = login.acBal.toDouble()
            user.onlineUser = login.onlineUser.toInt()
            user.loginSt = login.loginSt.toInt()
            user.sid = login.sid
            user.passKey = passKey
            ///adding to database
            DatabaseClient.getInstance(context).appDatabase.userDao().insert(user)
        }
        fun getUserInfo(context: Context, userId: String) : User{
            var user = User()
            if (DatabaseClient.getInstance(context).appDatabase != null) {
                return DatabaseClient.getInstance(context).appDatabase.userDao().getUser(userId)
            }
            return user
        }
        fun updateUser(context: Context, user: User){
            DatabaseClient.getInstance(context).appDatabase.userDao().update(user)
        }
        fun updateLogin(user: User) : Result{
            return Result(user.userId,
                    user.inactive.toString(),
                    user.fname,
                    user.lname,
                    if (user.dob == null) "" else user.dob,
                    user.emailId,
                    user.code,
                    user.mobileNo,
                    user.userType.toString(),
                    user.img,
                    if (user.fbImg == null) "" else user.fbImg,
                    user.enTime,
                    user.acBal.toString(),
                    user.acChipsBal.toString(),
                    user.loginSt.toString(),
                    user.onlineUser.toString(),
                    user.sid
            )
        }
        fun updateLoginViceVersa(login: Result) : User{
            val user = User()
            user.userId = login.id
            user.inactive = login.inactive.toInt()
            user.fname = login.fname
            user.lname = login.lname
            user.dob = login.dob
            user.emailId = login.emailId
            user.code = login.code
            user.mobileNo = login.mobileNo
            user.userType = login.userType.toInt()
            user.img = login.img
            user.fbImg = login.fbImg
            user.enTime = login.enTime
            user.acBal = login.acBal.toDouble()
            user.acChipsBal = login.acChipsBal.toDouble()
            user.loginSt = login.loginSt.toInt()
            user.onlineUser = login.onlineUser.toInt()
            user.sid = login.sid

            return user
        }
    }
}


