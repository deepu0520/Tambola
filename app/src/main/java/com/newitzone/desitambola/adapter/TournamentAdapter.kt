package com.newitzone.desitambola.adapter

import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.newitzone.desitambola.R
import com.newitzone.desitambola.databinding.CardviewTournamentItemBinding
import com.newitzone.desitambola.utils.UtilMethods
import model.tournament.Tournament
import java.util.Locale
import java.util.concurrent.TimeUnit

class TournamentAdapter(
    val items: List<Tournament>,
    private val date: String,
    private val day: String,
    val context: Context,
) : RecyclerView.Adapter<ViewHolderTournament>() {
    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTournament {
        val binding =
            CardviewTournamentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolderTournament(binding)
        //return ViewHolderTournament(LayoutInflater.from(context).inflate(R.layout.cardview_tournament_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ViewHolderTournament, position: Int) {
        if (items[position].amount.toDouble() == 0.0) {
            holder?.constraintLayout?.background = context.getDrawable(R.drawable.round_corner_1)
            holder?.tvTourName?.text = "Free Tournament"
            holder?.tvEntryFee?.text = "FREE"
        } else {
            holder?.constraintLayout?.background = context.getDrawable(R.drawable.round_corner)
            holder?.tvTourName?.text = items[position].name
            holder?.tvEntryFee?.text = "₹" + items[position].amount + "/-"
        }
        holder?.tvTourPrizeAmt?.text = "Total Tickets : " + items[position].totalRequestedTicket
        holder?.tvStartTime?.text =
            UtilMethods.getTimeAMPM(date + " " + items[position].startTime) + " " + day.toUpperCase()
        holder?.tvStatus?.text = context.getText(R.string.txt_fast_filling)
        if (items[position].requestOpen == "1") {
            // TODO: Join or cancel request
            if (items[position].userRequestedTickets.toInt() > 0) {
                // TODO: Cancel request
                holder?.tvJoinOrStatus?.text = "Withdraw"
                holder?.tvStatus?.text =
                    "You already join with " + items[position].userRequestedTickets + " ticket"
            } else {
                // TODO: Join request
                holder?.tvJoinOrStatus?.text = "JOIN"
                holder?.tvStatus?.text = context.getText(R.string.txt_fast_filling)
            }
        } else if (items[position].requestOpen == "0") {
            // TODO: Play or Game over
            val currentTime = System.currentTimeMillis()
            val tournamentTime = UtilMethods.getDateInMS(date + " " + items[position].startTime)
            var different = UtilMethods.getTimeDifferenceInMinute(currentTime, tournamentTime!!)
            if (different <= 2) {
                if (different >= -2) {
                    if (items[position].userRequestedTickets.toInt() > 0) {
                        // TODO: Cancel request
                        holder?.tvJoinOrStatus?.text = "Play"
                        holder?.tvStatus?.text =
                            "You already join with " + items[position].userRequestedTickets + " ticket"
                    } else {
                        // TODO: Time difference for play
                        holder?.tvJoinOrStatus?.text = "Registration closed"
                        holder?.tvStatus?.text = "Sorry you are late please join other tournament"
                    }
                } else if (different >= -5 && different < -2) {
                    if (items[position].userRequestedTickets.toInt() > 0) {
                        // TODO: Time difference for tournament play soon
                        holder?.tvJoinOrStatus?.text = "Be ready to play"
                        holder?.tvStatus?.text =
                            "You already join with " + items[position].userRequestedTickets + " ticket"
                    } else {
                        // TODO: Time difference for play
                        holder?.tvJoinOrStatus?.text = "Registration closed"
                        holder?.tvStatus?.text = "Sorry you are late please join other tournament"
                    }
                }
            } else if (different in 3..14) {
                // TODO: Time difference for tournament going
                holder?.tvJoinOrStatus?.text = "Tournament is going "
                holder?.tvStatus?.text = "Please wait for the result"
            } else if (different > 14) {
                // TODO: Time difference for result
                holder?.tvJoinOrStatus?.text = "Result"
                holder?.tvStatus?.text = "Tournament Over"
            }
        }

//        if (items[position].requestOpen == "1") {
//            if (items[position].userRequestedTickets.toInt() > 0){
//                //holder?.tvJoinOrStatus?.text = "You can play on "+UtilMethods.getTimeAMPM(date+" "+items[position].startTime)
//                holder?.tvJoinOrStatus?.text = "Long press to cancel"
//                holder?.tvStatus?.text = "You already join with "+items[position].userRequestedTickets+" ticket"
//            }else {
//                holder?.tvJoinOrStatus?.text = "JOIN"
//                holder?.tvStatus?.text = context.getText(R.string.txt_fast_filling)
//            }
//            holder?.lLTourItem.visibility = View.GONE
///*            // timer
////            // 60 seconds (1 minute)
////            val millisInFuture = UtilMethods.getDateInMS(date+" "+items[position].startTime)
////            // Count down interval 1 second
////            val countDownInterval: Long = 1000
////            if (millisInFuture != null) {
////                if (!items[position].timer) {
////                    items[position].timer = true
////                    //timer(millisInFuture, countDownInterval, holder?.tvRemainTime).start()
////                }
////            }
// */
//        }else{
//            holder?.tvJoinOrStatus?.text = "Tournament Over"
//            holder?.tvStatus?.text = "Tournament Ended"
//            holder?.lLTourItem.visibility = View.VISIBLE
//        }
    }

    // Method to configure and return an instance of CountDownTimer object
    private fun timer(
        millisInFuture: Long,
        countDownInterval: Long,
        tvTimer: TextView,
    ): CountDownTimer {
        return object : CountDownTimer(millisInFuture, countDownInterval) {

            override fun onTick(millisUntilFinished: Long) {
                val timeRemaining = timeString(millisUntilFinished)
                tvTimer.text = timeRemaining
                notifyDataSetChanged()
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onFinish() {
                tvTimer.text = "Joining time is over"
            }
        }
    }

    // Method to get days hours minutes seconds from milliseconds
    private fun timeString(millisUntilFinished: Long): String {
        var millisUntilFinished: Long = millisUntilFinished
        val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
        millisUntilFinished -= TimeUnit.DAYS.toMillis(days)

        val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
        millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
        millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

        // Format the string
        return String.format(Locale.getDefault(), "%02d H %02d M %02d S", hours, minutes, seconds)
    }
}

class ViewHolderTournament(view: CardviewTournamentItemBinding) :
    RecyclerView.ViewHolder(view.root) {
    // Holds the TextView that will add each animal to
    val constraintLayout = view.constraintLayout
    val tvTourName = view.textTourName
    val tvTourPrizeAmt = view.textTournamentPrizeAmt
    val tvEntryFee = view.textEntryFee
    val tvStartTime = view.textStartDatetime
    val tvRemainTime = view.textRemainingTime
    val tvJoinOrStatus = view.textJoinOrStatus
    val tvStatus = view.textStatus
    val lLTourItem = view.linearTournamentItem
    val mCountDownTimer: CountDownTimer? = null
}