package com.newitzone.tambola.utils

import android.util.Base64

/**
 * Created by imran on 11/04/2018.
 */
object Constants {
    const val API_CONTENT_TYPE = "application/json; charset=utf-8"
    const val API_BASE_PATH = "https://api.newitzone.com/tambola/"
    val API_AUTHORIZATION_KEY = "Basic "+ Base64.encodeToString("rakesh:15081990".toByteArray(), Base64.NO_WRAP);
}