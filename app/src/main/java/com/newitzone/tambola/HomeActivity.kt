package com.newitzone.tambola

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.snackbar.Snackbar
import com.newitzone.tambola.dialog.FullScreenDialog

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
    @BindView(R.id.image_combo) lateinit var imgCombo: ImageView
    @BindView(R.id.image_chips) lateinit var imgChips: ImageView
    // text view
    @BindView(R.id.text_coin_count) lateinit var tvCoinCount: TextView
    @BindView(R.id.text_gems_count) lateinit var tvGemsCount: TextView
    @BindView(R.id.text_power_count) lateinit var tvPowerCount: TextView
    @BindView(R.id.text_profile_name) lateinit var tvProfileName: TextView
    @BindView(R.id.text_level) lateinit var tvLevel: TextView

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
        tvCoinCount.setOnClickListener { view ->
            onCoin(view)
        }
        tvGemsCount.setOnClickListener { view ->
            onGems(view)
        }
        tvPowerCount.setOnClickListener { view ->
            onPower(view)
        }
        imgCombo.setOnClickListener { view ->
            onCombo(view)
        }
        imgChips.setOnClickListener { view ->
            onChips(view)
        }
    }
    fun onCash(view: View){
        //Snackbar.make(imgCash,"for Cash",Snackbar.LENGTH_SHORT).show()
        val dialog = FullScreenDialog()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        dialog.show(ft, FullScreenDialog.TAG)
    }
    fun onTournament(view: View){
        //Snackbar.make(imgTournament,"for Tournament",Snackbar.LENGTH_SHORT).show()
        val dialog = FullScreenDialog()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        dialog.show(ft, FullScreenDialog.TAG)
    }
    fun onPractice(view: View){
        //Snackbar.make(imgPractice,"for Practice",Snackbar.LENGTH_SHORT).show()
        val dialog = FullScreenDialog()
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        dialog.show(ft, FullScreenDialog.TAG)
    }
    fun onMenu(view: View){
        Snackbar.make(imgMenu,"for Menu",Snackbar.LENGTH_SHORT).show()
    }
    fun onCoin(view: View){
        Snackbar.make(tvCoinCount,"for coin",Snackbar.LENGTH_SHORT).show()
    }
    fun onGems(view: View){
        Snackbar.make(tvGemsCount,"for Gems",Snackbar.LENGTH_SHORT).show()
    }
    fun onPower(view: View){
        Snackbar.make(tvPowerCount,"for Power",Snackbar.LENGTH_SHORT).show()
    }

    fun onCombo(view: View){
        Snackbar.make(imgCombo,"for Combo",Snackbar.LENGTH_SHORT).show()
    }
    fun onChips(view: View){
        Snackbar.make(imgChips,"for Chips",Snackbar.LENGTH_SHORT).show()
    }


}
