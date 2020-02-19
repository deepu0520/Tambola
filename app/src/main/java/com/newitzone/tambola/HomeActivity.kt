package com.newitzone.tambola

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.add
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.snackbar.Snackbar
import com.newitzone.tambola.dialog.CashGamesDialog
import com.newitzone.tambola.dialog.TicketsDialog
import com.newitzone.tambola.dialog.TournamentGamesDialog

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class HomeActivity : AppCompatActivity() {
    private var context: Context? = null
    // image view
    @BindView(R.id.image_profile) lateinit var imgProfile: ImageView
    @BindView(R.id.image_cash) lateinit var imgCash: ImageView
    @BindView(R.id.image_tournament) lateinit var imgTournament: ImageView
    @BindView(R.id.image_practice) lateinit var imgPractice: ImageView
    @BindView(R.id.image_menu) lateinit var imgMenu: ImageView
    // text view
    @BindView(R.id.text_cash_balance) lateinit var tvCashAvailAmt: TextView
    @BindView(R.id.text_add_cash) lateinit var tvAddCash: TextView
    @BindView(R.id.text_profile_name) lateinit var tvProfileName: TextView

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

        //
        imgCash.setOnClickListener { view ->
            onCash(view)
        }
        imgTournament.setOnClickListener { view ->
            onTournament(view)
        }
        imgPractice.setOnClickListener { view ->
            onPractice(view)
        }
        imgMenu.setOnClickListener { view ->
            onMenu(view)
        }
        tvCashAvailAmt.setOnClickListener { view ->
            onCashAvailAmt(view)
        }
        tvAddCash.setOnClickListener { view ->
            onAddCash(view)
        }
    }
    fun onCash(view: View){
        //Snackbar.make(imgCash,"for Cash",Snackbar.LENGTH_SHORT).show()
        val dialog = CashGamesDialog()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        dialog.show(ft, CashGamesDialog.TAG)
    }
    fun onTournament(view: View){
        //Snackbar.make(imgTournament,"for Tournament",Snackbar.LENGTH_SHORT).show()
        val dialog = TournamentGamesDialog()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val args = Bundle()
        args?.putInt(KEY_CASH, 0)
        args?.putInt(KEY_TOURNAMENT, 1)
        dialog.setArguments(args)
        dialog.show(ft, TournamentGamesDialog.TAG)
    }
    fun onPractice(view: View){
        //Snackbar.make(imgPractice,"for Practice",Snackbar.LENGTH_SHORT).show()
        val dialog = TicketsDialog()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        dialog.show(ft, TicketsDialog.TAG)
    }
    fun onMenu(view: View){
        Snackbar.make(imgMenu,"for Menu",Snackbar.LENGTH_SHORT).show()
    }
    fun onCashAvailAmt(view: View){
        Snackbar.make(tvCashAvailAmt,"Available Cash amount is ₹"+tvCashAvailAmt.text,Snackbar.LENGTH_SHORT).show()
    }
    fun onAddCash(view: View){
        //Snackbar.make(tvGemsCount,"for Gems",Snackbar.LENGTH_SHORT).show()
        val intent = Intent(context, AddCashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }

    companion object {
        const val TAG = "HomeScreen"
        const val KEY_CASH = "Key_Cash"
        const val KEY_TOURNAMENT = "Key_Tournament"
    }
}
