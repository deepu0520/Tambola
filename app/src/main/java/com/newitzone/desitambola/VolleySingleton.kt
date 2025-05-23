package com.newitzone.desitambola

import android.app.Application
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

/**
 * Created by Rakesh on 04/08/2020.
 */
class VolleySingleton : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        FacebookSdk.sdkInitialize(applicationContext);
        AppEventsLogger.activateApp(this);
    }

    val requestQueue: RequestQueue? = null
        get() {
            if (field == null) {
                return Volley.newRequestQueue(applicationContext)
            }
            return field
        }

    fun <T> addToRequestQueue(request: Request<T>) {
        request.tag = TAG
        requestQueue?.add(request)
    }

    companion object {
        private val TAG = VolleySingleton::class.java.simpleName
        @get:Synchronized var instance: VolleySingleton? = null
            private set
    }
}