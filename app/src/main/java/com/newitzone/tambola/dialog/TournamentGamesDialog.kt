package com.newitzone.tambola.dialog

import android.content.Context
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
import com.newitzone.tambola.HomeActivity
import com.newitzone.tambola.R
import com.newitzone.tambola.adapter.TournamentAdapter
import com.newitzone.tambola.utils.RecyclerItemClickListenr
import com.newitzone.tambola.utils.UtilMethods
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
            tournamentApi(requireContext(), login.id, login.sid)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onPause() {
        super.onPause()
        if (login != null){
            tournamentApi(requireContext(), login.id, login.sid)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        state: Bundle?
    ): View? {
        super.onCreateView(inflater, parent, state)
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_tournament_games, parent, false)
        if (arguments != null) {
            val mArgs = arguments
            login = mArgs!!.getSerializable(HomeActivity.KEY_LOGIN) as Result
            keyModel = mArgs!!.getSerializable(HomeActivity.KEY_MODEL) as KeyModel
            //resTournament = mArgs!!.getSerializable(HomeActivity.KEY_TOURNAMENT) as ResTournament
        }
        ButterKnife.bind(this, view)
        val context: Context = requireContext()
        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
//        if (resTournament !=null) {
//            // TODO: Call To Load RecyclerView
//            onLoadRecyclerView(context, resTournament)
//        }
        return view
    }
    private fun onLoadRecyclerView(context: Context, resTournament: ResTournament){
        // Initializing an empty ArrayList to be filled with items
        val adapter = TournamentAdapter(resTournament.result.list, resTournament.result.date, resTournament.result.day, context)
        recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        recyclerView.adapter = adapter
        recyclerView.addOnItemTouchListener(RecyclerItemClickListenr(context, recyclerView, object : RecyclerItemClickListenr.OnItemClickListener {

            override fun onItemClick(view: View, position: Int) {
                val tournamentId = resTournament.result.list[position].id
                val amt = resTournament.result.list[position].amount
                val reqOpen = resTournament.result.list[position].requestOpen.toInt()
                val userReqTickets = resTournament.result.list[position].userRequestedTickets.toInt()
                // set tournament id in key model
                keyModel.tournamentId = tournamentId
                keyModel.amount = amt.toFloat()

                if (reqOpen == 1 && userReqTickets <= 0) {
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

                }else if(reqOpen == 1 && userReqTickets > 0){
                    // TODO: Automatic time enable checking
                    if (UtilMethods.isAutoTime(context) == 1) {

                        val currentTime = System.currentTimeMillis()
                        val tournamentTime =
                            UtilMethods.getDateInMS(resTournament.result.date + " " + resTournament.result.list[position].startTime)
                        var different = 0L
                        if (currentTime > tournamentTime!!) {
                            different = currentTime.minus(tournamentTime)
                        } else if (tournamentTime!! > currentTime) {
                            different = tournamentTime?.minus(currentTime)
                        }
                        val secondsInMilli: Long = 1000
                        val minutesInMilli = secondsInMilli * 60

                        val elapsedMinutes: Long = different / minutesInMilli
                        different %= minutesInMilli

                        if (elapsedMinutes < 5) {
                            // TODO: start the game
                            UtilMethods.ToastLong(context,"Your game is started in several minutes")
                        } else {
                            UtilMethods.ToastLong(
                                context,
                                "Your game start on " + UtilMethods.getTimeAMPM(resTournament.result.date + " " + resTournament.result.list[position].startTime)
                            )
                        }
                    }else{
                        UtilMethods.ToastLong(context,"Please enable automatic time of your device")
                    }
                }
            }
            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onItemLongClick(view: View?, position: Int) {
                //TODO : ("cancel request")
                val tournamentId = resTournament.result.list[position].id
                val gameId = resTournament.result.list[position].game_id
                val reqOpen = resTournament.result.list[position].requestOpen.toInt()
                val userReqTickets = resTournament.result.list[position].userRequestedTickets.toInt()

                if(reqOpen == 1 && userReqTickets > 0){
                    // TODO: show dialog to cancel request
                    dialogAlertCancel(context, login.id, login.sid, tournamentId, gameId)
                }
            }
        }))
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun dialogAlertCancel(context: Context, userId: String, sesId: String, tournamentId: String, gameId: String){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.app_name)
        builder.setMessage("Do you want to cancel request of tournament game?")
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