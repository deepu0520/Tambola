package com.newitzone.tambola.dialog

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newitzone.tambola.*
import com.newitzone.tambola.utils.SharedPrefManager
import com.newitzone.tambola.utils.UtilMethods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.KeyModel
import model.login.Result
import retrofit.TambolaApiService
import retrofit2.HttpException

class TicketsDialog : DialogFragment() {
    @JvmField
    @BindView(R.id.image_ticket_1)
    var imgTicket1: ImageView? = null
    @JvmField
    @BindView(R.id.image_ticket_2)
    var imgTicket2: ImageView? = null

    private lateinit var login: Result
    private lateinit var keyModel: KeyModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        state: Bundle?
    ): View? {
        super.onCreateView(inflater, parent, state)
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_tickets, parent, false)
        if (arguments != null) {
            val mArgs = arguments
            login = mArgs!!.getSerializable(HomeActivity.KEY_LOGIN) as Result
            keyModel = mArgs!!.getSerializable(HomeActivity.KEY_MODEL) as KeyModel
        }
        ButterKnife.bind(this, view)
        return view
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.image_ticket_1)
    fun onTicket1(view: View?) {
        val reqTicket = 1
        callApi(reqTicket)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.image_ticket_2)
    fun onTicket2(view: View?) {
        val reqTicket = 2
        callApi(reqTicket)
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun callApi(req_ticket: Int){
        val context= requireContext()
        val userId = SharedPrefManager.getInstance(context).result.id
        val sessionId = SharedPrefManager.getInstance(context).result.sid
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
                val response = if(keyModel.gameType == PlayActivity.TOURNAMENT_GAME)
                                    service.gameRequestTournament(userid,sesid,amt,req_ticket.toString(),game_type,tournament_id)
                               else
                                    service.gameRequest(userid,sesid,amt,req_ticket.toString(),game_type,tournament_id)
                withContext(Dispatchers.Main) {
                    try {
                        if (response.isSuccessful) {
                            if (response.code() == 201) {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
                                if (keyModel.gameType == PlayActivity.TOURNAMENT_GAME){
                                    UtilMethods.ToastLong(context, "You successfully join for the Tournament game with ticket $req_ticket")

                                    //TODO: Call Ticket Dialog
                                    val dialogT = TournamentGamesDialog()
                                    val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
                                    val args = Bundle()
                                    args?.putSerializable(HomeActivity.KEY_LOGIN, login)
                                    args?.putSerializable(HomeActivity.KEY_MODEL, keyModel)
                                    dialogT.arguments = args
                                    dialogT.show(ft, TicketsDialog.TAG)
                                    // TODO: Current dialog dismiss
                                    dialog?.dismiss()
                                }else {
                                    keyModel.gameRequestId = response.body()?.result?.get(0)!!.requestID
                                    // redirect to loading screen by intent
                                    val intent = Intent(activity, LoadingActivity::class.java)
                                    intent.putExtra(HomeActivity.KEY_MODEL, keyModel)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    activity!!.startActivity(intent)
                                    // TODO: Current dialog dismiss
                                    dialog?.dismiss()
                                }
                            } else {
                                UtilMethods.ToastLong(context, "${response.body()?.msg}")
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

    companion object {
        const val TAG = "FullScreenDialog"
        const val KEY_TICKET = "Key_Ticket"
    }
}