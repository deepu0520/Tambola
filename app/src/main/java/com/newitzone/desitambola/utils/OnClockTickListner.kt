package com.newitzone.desitambola.utils

import android.text.format.Time

interface OnClockTickListner {
    fun OnSecondTick(currentTime: Time?)
    fun OnMinuteTick(currentTime: Time?)
}