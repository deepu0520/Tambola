package com.newitzone.tambola.utils

import android.content.Context
import model.login.Result

class SharedPrefManager private constructor(private val mCtx: Context) {

    val isLoggedIn: Boolean
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getInt("login_st", 0) != 0
        }

    val result: Result
        get() {
            val sPref = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return Result(
                sPref.getString("id", "").toString(),
                sPref.getInt("inactive", 0),
                sPref.getString("fname", "").toString(),
                sPref.getString("lname", "").toString(),
                sPref.getString("dob", "").toString(),
                sPref.getString("email_id", "").toString(),
                sPref.getString("code", "").toString(),
                sPref.getString("mobile_no", "").toString(),
                sPref.getString("user_type", "").toString(),
                sPref.getString("img", "").toString(),
                sPref.getString("en_time", "").toString(),
                sPref.getFloat("AcBal", 0f),
                sPref.getInt("onlineUser", 0),
                sPref.getInt("login_st", 0),
                sPref.getString("sid", "").toString()
            )
        }


    fun saveUser(result: Result) {

        val sPref = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sPref.edit()

        editor.putString("id", result.id)
        editor.putInt("inactive", result.inactive)
        editor.putString("fname", result.fname)
        editor.putString("lname", result.lname)
        editor.putString("dob", result.dob)
        editor.putString("email_id", result.email_id)
        editor.putString("code", result.code)
        editor.putString("mobile_no", result.mobile_no)
        editor.putString("user_type", result.user_type)
        editor.putString("img", result.img)
        editor.putString("en_time", result.en_time)
        editor.putFloat("AcBal", result.acBal)
        editor.putInt("onlineUser", result.onlineUser)
        editor.putInt("login_st", result.login_st)
        editor.putString("sid", result.sid)

        editor.apply()

    }

    fun clear() {
        val sPref = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sPref.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private val SHARED_PREF_NAME = "my_shared_preff"
        private var mInstance: SharedPrefManager? = null
        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }
    }

}