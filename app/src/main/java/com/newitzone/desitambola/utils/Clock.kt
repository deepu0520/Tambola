package com.newitzone.desitambola.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.SystemClock
import android.text.format.Time
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*

class Clock {
    companion object{
        const val TICKPERSECOND = 0
        const val TICKPERMINUTE = 1
    }

    private var Time: Time? = null
    private val TimeZone: TimeZone? = null
    private var Handler: Handler? = null
    private val OnClockTickListenerList: MutableList<OnClockTickListner> =
        ArrayList<OnClockTickListner>()

    private var Ticker: Runnable? = null

    private var IntentReceiver: BroadcastReceiver? = null
    private var IntentFilter: IntentFilter? = null

    private var TickMethod = 0
    var mContext: Context? = null

    fun Clock(context: Context?) {
        Clock(context, Clock.TICKPERMINUTE)
    }

    private fun Clock(context: Context?, tickMethod: Int) {
        mContext = context
        TickMethod = tickMethod
        Time = Time()
        Time!!.setToNow()
        when (TickMethod) {
            0 -> StartTickPerSecond()
            1 -> StartTickPerMinute()
            else -> {
            }
        }
    }

    private fun Tick(tickInMillis: Long) {
        Time!!.set(Time!!.toMillis(true) + tickInMillis)
        NotifyOnTickListners()
    }

    private fun NotifyOnTickListners() {
        when (TickMethod) {
            0 -> for (listner in OnClockTickListenerList) {
                listner.OnSecondTick(Time)
            }
            1 -> for (listner in OnClockTickListenerList) {
                listner.OnMinuteTick(Time)
            }
        }
    }

    private fun StartTickPerSecond() {
        Handler = Handler()
        Ticker = Runnable {
            Tick(1000)
            val now = SystemClock.uptimeMillis()
            val next = now + (1000 - now % 1000)
            Handler!!.postAtTime(Ticker, next)
        }
        Ticker!!.run()
    }

    private fun StartTickPerMinute() {
        IntentReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Tick(60000)
            }
        }
        IntentFilter = IntentFilter()
        IntentFilter!!.addAction(Intent.ACTION_TIME_TICK)
        mContext!!.registerReceiver(IntentReceiver, IntentFilter, null, Handler)
    }

    fun StopTick() {
        if (IntentReceiver != null) {
            mContext!!.unregisterReceiver(IntentReceiver)
            //LocalBroadcastManager.getInstance(mContext!!).unregisterReceiver(IntentReceiver!!);
        }
        if (Handler != null) {
            Handler!!.removeCallbacks(Ticker)
        }
    }

    fun GetCurrentTime(): Time? {
        return Time
    }

    fun AddClockTickListner(listner: OnClockTickListner) {
        OnClockTickListenerList.add(listner)
    }

}