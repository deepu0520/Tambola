package com.newitzone.tambola.utils

import android.content.Context
import android.util.Base64
import java.io.IOException

/**
 * Created by rakesh on 22/02/2020.
 */
object Constants {
    const val API_CONTENT_TYPE = "application/json; charset=utf-8"
    const val API_BASE_PATH = "https://api.newitzone.com/tambola/"
    val API_AUTHORIZATION_KEY = "Basic "+ Base64.encodeToString("rakesh:15081990".toByteArray(), Base64.NO_WRAP);

    const val M_ID = "Nqdtmu49156635409548"//"GblQDP68212852342989" //Paytm Merchand Id we got it in paytm credentia// ls
    const val M_KEY = "EZGpToHUdjPz#Sqj"//"#sFadf3f9Xz6k79R" //Paytm Merchand Key we got it in paytm credentia// ls
    const val CHANNEL_ID = "WAP" //Paytm Channel Id, got it in paytm credentials
    const val INDUSTRY_TYPE_ID = "Retail" //Paytm industry type got it in paytm credential
    const val WEBSITE = "newitzone"
    const val CALLBACK_URL = "https://securegw.paytm.in/theia/paytmCallback"
//    private val WEBSITE = "APPSTAGING"
//    private val CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=ORDER12345"

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }
}