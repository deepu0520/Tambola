package com.newitzone.desitambola.dialog

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.newitzone.desitambola.HomeActivity
import com.newitzone.desitambola.LoadingTournamentGameActivity
import com.newitzone.desitambola.R
import com.newitzone.desitambola.adapter.TournamentAdapter
import com.newitzone.desitambola.utils.Constants
import com.newitzone.desitambola.utils.RecyclerItemClickListenr
import com.newitzone.desitambola.utils.UtilMethods
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import model.KeyModel
import model.login.Result
import model.tournament.ResTournament
import retrofit.TambolaApiService


class TournamentGamesDialog : DialogFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var login: Result
    private lateinit var keyModel: KeyModel
    private var isRunning = true
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        if (login != null){
//            context?.let { onLoadRecyclerView(it, getTournamentStartJson()) }
            isRunning = true
            tournamentApi(requireContext(), login.id, login.sid)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPause() {
        super.onPause()
        if (login != null){
//            context?.let { onLoadRecyclerView(it, getTournamentStartJson()) }
            isRunning = false
            tournamentApi(requireContext(), login.id, login.sid)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        state: Bundle?
    ): View? {
        super.onCreateView(inflater, parent, state)
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_tournament_games, parent, false)
        if (arguments != null) {
            val mArgs = arguments
            login = mArgs!!.getSerializable(HomeActivity.KEY_LOGIN) as Result
            keyModel = mArgs!!.getSerializable(HomeActivity.KEY_MODEL) as KeyModel
            //resTournament = mArgs!!.getSerializable(HomeActivity.KEY_TOURNAMENT) as ResTournament
        }
        ButterKnife.bind(this, view)
        val context: Context = requireContext()
        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        return view
    }
    private fun onLoadRecyclerView(context: Context, resTournament: ResTournament){
        // Initializing an empty ArrayList to be filled with items
        val adapter = TournamentAdapter(resTournament.result.list, resTournament.result.date, resTournament.result.day, context)
        recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerView.adapter = adapter
        recyclerView.addOnItemTouchListener(RecyclerItemClickListenr(context, recyclerView, object : RecyclerItemClickListenr.OnItemClickListener {

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onItemClick(view: View, position: Int) {
                val tournament = resTournament.result.list[position]
                val tournamentId = resTournament.result.list[position].id
                val gameId = resTournament.result.list[position].game_id
                val amt = resTournament.result.list[position].amount
                val reqOpen = resTournament.result.list[position].requestOpen.toInt()
                val userReqTickets = resTournament.result.list[position].userRequestedTickets.toInt()
                // set tournament id in key model
                keyModel.tournamentId = tournamentId
                keyModel.amount = amt.toFloat()
                // TODO: Automatic time enable checking
                if (UtilMethods.isAutoTime(context) == 1) {
                    if (reqOpen == 1) {
                        // TODO: if request is open
                        if (userReqTickets <= 0) {
                            //TODO: Join the tournament
                            //TODO: Call Ticket Dialog
                            val dialogTicket = TicketsDialog()
                            val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
                            val args = Bundle()
                            args?.putSerializable(HomeActivity.KEY_LOGIN, login)
                            args?.putSerializable(HomeActivity.KEY_MODEL, keyModel)
                            dialogTicket.arguments = args
                            dialogTicket.show(ft, TicketsDialog.TAG)
                            // TODO: dismiss current dialog
                            dialog?.dismiss()
                        }
                        else if (userReqTickets > 0) {
                            // TODO: Cancel Request
                            val currentTime = System.currentTimeMillis()
                            val tournamentTime = UtilMethods.getDateInMS(resTournament.result.date + " " + resTournament.result.list[position].startTime)
                            // TODO: Time difference
                            var different = UtilMethods.getTimeDifferenceInMinute(currentTime,tournamentTime!!)

                            if (different < -5) {
                                // TODO: time difference is greater than 5 minutes
                                // TODO: show dialog to cancel request
                                dialogAlertCancel(
                                    context,
                                    login.id,
                                    login.sid,
                                    tournamentId,
                                    gameId
                                )
                            }
                        }
                    } else if (reqOpen == 0) {
                        // TODO: if request is close
                        val currentTime = System.currentTimeMillis()
                        val tournamentTime = UtilMethods.getDateInMS(resTournament.result.date + " " + resTournament.result.list[position].startTime)

                        // TODO: Time difference for play
                        var different = UtilMethods.getTimeDifferenceInMinute(currentTime, tournamentTime!!)
                        if (different <= 2){
                            if (different >= -2) {
                                // TODO: time difference is 2 minute and less than equal to 2
                                if (userReqTickets > 0) {
                                    // TODO: start the game
                                    //UtilMethods.ToastLong(context,"Your game is started in several minutes")
                                    val intent = Intent(context, LoadingTournamentGameActivity::class.java)
                                    intent.putExtra(HomeActivity.KEY_LOGIN, login)
                                    intent.putExtra(HomeActivity.KEY_TOURNAMENT, tournament)
                                    intent.putExtra(HomeActivity.KEY_TOURNAMENT_TIMER, UtilMethods.getTimeDifferenceInSec(currentTime, tournamentTime!!))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                    startActivity(intent)
                                    // TODO:
                                    dialog?.dismiss()
                                } else {
                                    MessageDialog(context,"Alert","You have not participate this Tournament").show()
                                }
                            }else if (different >= -5 && different < -2){
                                // TODO: Time difference for tournament play soon
                                MessageDialog(context,"Alert","Tournament will start in few minutes, Be ready").show()
                            }
                        }else if (different in 3..14){
                            // TODO: Time difference for tournament going
                            MessageDialog(context,"Alert","Please wait...for the result").show()
                        } else if (different > 14) {
                            // TODO: Time difference for tournament result
                            MessageDialog(context, "Alert", "Result is coming soon.....").show()
                        }
                    }
                } else {
                    MessageDialog(context,"Alert","Please enable automatic time of your device").show()
                }
            }
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onItemLongClick(view: View?, position: Int) {
                //TODO : "cancel request"
            }
        }))
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun dialogAlertCancel(context: Context, userId: String, sesId: String, tournamentId: String, gameId: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Do you want to withdraw with this tournament game?")
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            dialog.dismiss()
            cancelRequestTournamentApi(context, userId, sesId, tournamentId, gameId)
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            //Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.show()
    }
    // TODO: tournament Api
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun tournamentApi(context: Context, userId: String, sesId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.getTournament(userId, sesId)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body()?.status == 1) {
                                    // TODO: Call to load RecyclerView
                                    onLoadRecyclerView(context, response.body()!!)
                                }else{
                                    UtilMethods.ToastLong(context,"Msg:${response.body()?.msg}")
                                }
                            } else {
                                UtilMethods.ToastLong(context,"Error: ${response.code()}" + "\nMsg:${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")

                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
                        }
                        UtilMethods.hideLoading()
                    }
                }catch (e: Throwable) {
                    Log.e("TAG","Throwable : $e")
                    UtilMethods.hideLoading()
                }
            }
        }else{
            UtilMethods.ToastLong(context,"No Internet Connection")
        }
    }
    // TODO: Tournament Start Json by local
    private fun getTournamentStartJson(): ResTournament {
        val jsonFileString = context?.let { Constants.getJsonDataFromAsset(it, "tournamentList.json") }
        Log.i("data", jsonFileString)

        val gson = Gson()
        val tournament = object : TypeToken<ResTournament>() {}.type

        return gson.fromJson(jsonFileString, tournament)
    }
    // TODO: cancel request tournament Api
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun cancelRequestTournamentApi(context: Context, userId: String, sesId: String, tournament_id: String, game_id: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            UtilMethods.showLoading(context)
            val service = TambolaApiService.RetrofitFactory.makeRetrofitService()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = service.gameRequestTournamentCancel(userId, sesId, tournament_id, game_id)
                    withContext(Dispatchers.Main) {
                        try {
                            if (response.isSuccessful) {
                                if (response.body()?.status == 1) {
                                    UtilMethods.hideLoading()
                                    // TODO: cancel request response
                                    UtilMethods.ToastLong(context,"${response.body()?.msg}")
                                    onResume()
                                }else{
                                    UtilMethods.ToastLong(context,"${response.body()?.msg}")
                                }
                            } else {
                                UtilMethods.ToastLong(context,"Error: ${response.code()}" + "\nMsg:${response.body()?.msg}")
                            }
                        } catch (e: Exception) {
                            UtilMethods.ToastLong(context, "Exception ${e.message}")

                        } catch (e: Throwable) {
                            UtilMethods.ToastLong(context,"Oops: Something else went wrong : " + e.message)
                        }
                        UtilMethods.hideLoading()
                    }
                }catch (e: Throwable) {
                    Log.e("TAG","Throwable : $e")
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