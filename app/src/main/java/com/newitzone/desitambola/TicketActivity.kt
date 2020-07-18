package com.newitzone.desitambola

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.newitzone.desitambola.adapter.TournamentAdapter
import com.newitzone.desitambola.dialog.CashGamesDialog
import com.newitzone.desitambola.dialog.MessageDialog
import com.newitzone.desitambola.dialog.TicketsDialog
import com.newitzone.desitambola.dialog.TournamentGamesDialog
import com.newitzone.desitambola.utils.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.KeyModel
import model.login.Result
import model.tournament.ResTournament
import retrofit.TambolaApiService
import retrofit2.HttpException
import java.lang.Exception
import kotlin.math.abs

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class TicketActivity : AppCompatActivity() {
    private var context: Context? = null
    private lateinit var login: Result
    private lateinit var keyModel: KeyModel
    @BindView(R.id.image_ticket_1) lateinit var imgTicket1: ImageView
    @BindView(R.id.image_ticket_2) lateinit var imgTicket2: ImageView
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.dialog_tickets)
        supportActionBar?.hide()
        this.context = this@TicketActivity
        ButterKnife.bind(this)
        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()
        // Intent
        login = intent.getSerializableExtra(HomeActivity.KEY_LOGIN) as Result
        keyModel = intent.getSerializableExtra(HomeActivity.KEY_MODEL) as KeyModel

    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.image_ticket_1)
    fun onTicket1(view: View?) {
        if (login != null && keyModel != null) {
            if (login.acBal.toDouble() >= keyModel.amount.toDouble()) {
                val reqTicket = 1
                callApi(reqTicket)
            }else{
                val context = this@TicketActivity
                MessageDialog(context,"Low balance", "Insufficient balance in your account to join Tournament game with 1 Ticket").show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.image_ticket_2)
    fun onTicket2(view: View?) {
        if (login != null && keyModel != null) {
            val gameAmt = (keyModel.amount * 2)
            if (login.acBal.toDouble() >= gameAmt.toDouble()) {
                val reqTicket = 2
                callApi(reqTicket)
            }else{
                val context = this@TicketActivity
                MessageDialog(context,"Low balance", "Insufficient balance in your account to join Tournament game with 2 Tickets").show()
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callApi(req_ticket: Int){
        val context = this@TicketActivity
        val userId = login.id
        val sessionId = login.sid
        // add ticket type
        keyModel.ticketType = req_ticket
        if (sessionId.isNotBlank()) {
            //TODO: Use this
            gameRequestApi(
                context,
                userId,
                sessionId,
                keyModel.amount.toString(),
                keyModel.ticketType,
                keyModel.gameType.toString(),
                keyModel.tournamentId
            )
        }else{
            UtilMethods.ToastLong(context,"Session expired")
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun gameRequestApi(context: Context, userid: String, sesid: String
                               , amt: String, req_ticket: Int
                               , game_type: String, tournament_id: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                val response = service.gameRequestTournament(userid,sesid,amt,req_ticket.toString(),game_type,tournament_id)
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            if (response.body()?.status == 1) {

                                UtilMethods.ToastLong(context, "You are successfully join for the Tournament game with ticket $req_ticket")
                                val intent = Intent(context, TournamentGameActivity::class.java)
                                intent.putExtra(HomeActivity.KEY_LOGIN, login)
                                intent.putExtra(HomeActivity.KEY_MODEL, keyModel)
                                setResult(TournamentGameActivity.REQ_CODE, intent)
                                finish()
                            } else {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                                finish()
                            }
                        } else {
                            UtilMethods.ToastLong(context, "${response?.message()}")
                        }
                    } catch (e: HttpException) {
                        UtilMethods.ToastLong(context, "Exception ${e.message}")
                    } catch (e: Throwable) {
                        UtilMethods.ToastLong(
                            context,
                            "Ooops: Something else went wrong : " + e.message
                        )
                    }
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
                finish()
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}
