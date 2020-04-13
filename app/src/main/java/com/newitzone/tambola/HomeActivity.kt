package com.newitzone.tambola

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentTransaction
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.snackbar.Snackbar
import com.newitzone.tambola.dialog.CashGamesDialog
import com.newitzone.tambola.dialog.TicketsDialog
import com.newitzone.tambola.dialog.TournamentGamesDialog
import com.newitzone.tambola.utils.KCustomToast
import com.newitzone.tambola.utils.SharedPrefManager
import com.newitzone.tambola.utils.UtilMethods
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.KeyModel
import model.login.ResLogin
import model.login.Result
import retrofit.TambolaApiService
import java.lang.Exception

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class HomeActivity : AppCompatActivity() {
    private var context: Context? = null
    private lateinit var login: Result
    // image view
    @BindView(R.id.image_profile) lateinit var imgProfile: ImageView
    @BindView(R.id.image_cash) lateinit var imgCash: ImageView
    @BindView(R.id.image_tournament) lateinit var imgTournament: ImageView
    @BindView(R.id.image_practice) lateinit var imgPractice: ImageView
    @BindView(R.id.image_menu) lateinit var imgMenu: ImageView
    // text view
    @BindView(R.id.text_cash_balance) lateinit var tvCash: TextView
    @BindView(R.id.text_chips_balance) lateinit var tvChips: TextView
    @BindView(R.id.text_profile_name) lateinit var tvProfileName: TextView
    @BindView(R.id.text_online_count) lateinit var tvOnlineCount: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide()
        this.context = this@HomeActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        // Intent
        login = intent.getSerializableExtra(HomeActivity.KEY_LOGIN) as Result
        if (login != null) {
            // TODO: Load Account Details
            onLoadAccountDetails(login)
        }

        // TODO: Profile update
        imgProfile.setOnClickListener { view ->
            onProfileUpdate()
        }
        // TODO: Cash game
        imgCash.setOnClickListener { view ->
            onCash(view)
        }
        // TODO: Tournament game
        imgTournament.setOnClickListener { view ->
            onTournament(view)
        }
        // TODO: Practice game
        imgPractice.setOnClickListener { view ->
            onPractice(view)
        }
        // TODO: Menu
        imgMenu.setOnClickListener { view ->
            onMenu(view)
        }
        // TODO: Add Cash
        tvCash.setOnClickListener { view ->
            onCashAvailAmt(view)
        }
        // TODO: Add Chips
        tvChips.setOnClickListener { view ->
            onChips(view)
        }
    }
    private fun onLoadAccountDetails(login: Result){
        tvProfileName.text = login.fname+" "+login.lname
        tvCash.text = "₹"+UtilMethods.roundOffDecimal(login.acBal).toString()
        tvChips.text = UtilMethods.roundOffDecimal(login.acChipsBal.toFloat()).toString()
        tvOnlineCount.text = login.onlineUser.toString()
        if (login.img.isNotEmpty()) {
            // load the image with Picasso
            Picasso.get().load(login.img).into(imgProfile) // select the ImageView to load it into
        }
    }
    private fun onProfileUpdate(){
        val intent = Intent(context, ProfileActivity::class.java)
        intent.putExtra(KEY_LOGIN, login)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }
    private fun onCash(view: View){
        if (login.acBal.toDouble() > 5) {
            var keyModel = KeyModel("", 0, 5f, 0, "")
            keyModel.gameType = 1

            val dialog = CashGamesDialog()
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            val args = Bundle()
            args?.putSerializable(KEY_MODEL, keyModel)
            dialog.arguments = args
            dialog.show(ft, CashGamesDialog.TAG)
        }else{
            Snackbar.make(tvChips,"Insufficient balance in your account to play Cash game",Snackbar.LENGTH_SHORT).show()
            //KCustomToast.toastWithFont(context as Activity, "Insufficient balance in your account to play Cash game")
        }
    }
    private fun onTournament(view: View){
        if (login.acBal.toDouble() > 5) {
            var keyModel = KeyModel("", 0, 0f, 1, "")
            keyModel.gameType = 2

            val dialog = TournamentGamesDialog()
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            val args = Bundle()
            args?.putSerializable(KEY_MODEL, keyModel)
            dialog.arguments = args
            dialog.show(ft, TournamentGamesDialog.TAG)
        }else{
            val context = this@HomeActivity
            //UtilMethods.ToastLong(context,"Insufficient balance in your account to play Tournament game")
            Snackbar.make(tvChips,"Insufficient balance in your account to play Tournament game",Snackbar.LENGTH_SHORT).show()
            //KCustomToast.toastWithFont(context, "Insufficient balance in your account to play Tournament game")
//            runOnUiThread {
//                KCustomToast.toastWithFont(context, "Insufficient balance in your account to play Tournament game")
//            }
        }
    }
    private fun onPractice(view: View){
        if (login.acChipsBal.toDouble() > 10) {
            var keyModel = KeyModel("", 0, 10f, 0, "")
            keyModel.gameType = 0

            val dialog = TicketsDialog()
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            val args = Bundle()
            args?.putSerializable(KEY_MODEL, keyModel)
            dialog.arguments = args
            dialog.show(ft, TicketsDialog.TAG)
        }else{
            Snackbar.make(tvChips,"Insufficient chips in your account to play Sample game", Snackbar.LENGTH_SHORT).show()
            //KCustomToast.toastWithFont(context as Activity, "Insufficient chips in your account to play Sample game")
        }
    }
    private fun onMenu(view: View){
        showPopupMenu(view)
    }
    private fun onCashAvailAmt(view: View){
        //Snackbar.make(tvCash,"Available Cash amount is ₹"+tvCash.text,Snackbar.LENGTH_SHORT).show()
        val intent = Intent(context, AddCashActivity::class.java)
        intent.putExtra(KEY_LOGIN, login)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }
    private fun onChips(view: View){
        //Snackbar.make(tvChips,"Available Chips is "+tvChips.text,Snackbar.LENGTH_SHORT).show()
        KCustomToast.toastWithFont(context as Activity, "Available Chips is "+tvChips.text)
//        val intent = Intent(context, AddCashActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
//        startActivity(intent)
    }
    private fun showPopupMenu(view: View) {
        var popup: PopupMenu? = null;
        popup = PopupMenu(this, view)
        popup.inflate(R.menu.setting_menu)

        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.menu_setting -> {
                    Toast.makeText(this@HomeActivity, item.title, Toast.LENGTH_SHORT).show();
                }
                R.id.menu_sound -> {
                    Toast.makeText(this@HomeActivity, item.title, Toast.LENGTH_SHORT).show();
                }
                R.id.menu_logout -> {
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
            }

            true
        })
        try {
            popup.show()
        }catch (e: Exception){
            Log.e("TAG","Exception: ${e.toString()}")
        }
    }

    override fun onResume() {
        super.onResume()
        if (SharedPrefManager.getInstance(context as HomeActivity).isLoggedIn) {
            loginApi(
                context as HomeActivity
                , SharedPrefManager.getInstance(context as HomeActivity).result.emailId
                , SharedPrefManager.getInstance(context as HomeActivity).passKey.toString()
                , SharedPrefManager.getInstance(context as HomeActivity).result.userType
                , "0"
                , SharedPrefManager.getInstance(context as HomeActivity).result.sid
                , SharedPrefManager.getInstance(context as HomeActivity).result.id
            )
        }
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK ->  {
                // do something here
                context?.let { dialogExit(it) }
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
    private fun dialogExit(context: Context){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Do you want to exit the game?")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->

            finish()
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            //Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.show()
    }
    companion object {
        const val TAG = "HomeScreen"
        const val KEY_LOGIN = "Key_Login"
        const val KEY_MODEL = "Key_Model"
        const val KEY_GAME_IN = "Key_Game_In"
    }

    // TODO: login Api
    private fun loginApi(
        context: Context, userID: String, passKey: String
        , userType: String, loginType: String, sesId: String, userId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            //UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.getlogin(userID, passKey, userType, loginType, sesId, userId)
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            onLoadAccountDetails(response.body()?.result?.get(0)!!)
                        } else {
                            UtilMethods.ToastLong(context,"Error: ${response.code()}"+"\nMsg:${response.body()?.msg}")
                        }
                    } catch (e: Exception) {
                        UtilMethods.ToastLong(context,"Exception ${e.message}")

                    } catch (e: Throwable) {
                        UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
                    }
                    //UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
}
