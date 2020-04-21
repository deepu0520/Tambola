package com.newitzone.tambola.dialog

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.newitzone.tambola.HomeActivity
import com.newitzone.tambola.PlayActivity
import com.newitzone.tambola.R
import com.newitzone.tambola.adapter.PriceAdapter
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
import java.lang.Exception

class TournamentGamesDialog : DialogFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var login: Result
    private lateinit var keyModel: KeyModel
    private lateinit var resTournament: ResTournament
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
            resTournament = mArgs!!.getSerializable(HomeActivity.KEY_TOURNAMENT) as ResTournament
        }
        ButterKnife.bind(this, view)
        val context: Context = requireContext()
        recyclerView = view.findViewById(R.id.recycler_view) as RecyclerView
        if (resTournament !=null) {
            // TODO: Call To Load RecyclerView
            onLoadRecyclerView(context, resTournament)
        }
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

                    //TODO: Call Ticket Dialog
                    val dialog = TicketsDialog()
                    val ft: FragmentTransaction = activity!!.supportFragmentManager.beginTransaction()
                    val args = Bundle()
                    args?.putSerializable(HomeActivity.KEY_MODEL, keyModel)
                    dialog.arguments = args
                    dialog.show(ft, TicketsDialog.TAG)
                }
            }
            override fun onItemLongClick(view: View?, position: Int) {
                TODO("do nothing")
            }
        }))
    }
    // TODO: tournament Api
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun tournamentApi(context: Context, userId: String, sesId: String){
        if (UtilMethods.isConnectedToInternet(context)) {
            //UtilMethods.showLoading(context)
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
                        //UtilMethods.hideLoading()
                    }
                }catch (e: Throwable) {
                    Log.e("TAG","Throwable : $e")
                    //UtilMethods.hideLoading()
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