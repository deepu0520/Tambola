package com.newitzone.tambola.utils

import android.content.Context
import model.KeyModel
import model.login.Result

class SharedPrefManager private constructor(private val mCtx: Context) {

    val isLoggedIn: Boolean
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return !sharedPreferences.getString("id", "").isNullOrBlank()
        }
    val passKey: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("pass_key", "")
        }
    val userId: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("id", "")
        }
    val result: Result
        get() {
            val sPref = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return Result(
                sPref.getString("id", "").toString(),
                sPref.getString("inactive", "0").toString(),
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
                sPref.getString("AcChipsBal", "").toString(),
                sPref.getString("onlineUser", "0").toString(),
                sPref.getString("login_st", "0").toString(),
                sPref.getString("sid", "00000000-0000-0000-0000-000000000000").toString()
            )
        }


    fun saveUser(result: Result,passKey: String) {

        val sPref = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sPref.edit()

        editor.putString("id", result.id)
        editor.putString("inactive", result.inactive)
        editor.putString("fname", result.fname)
        editor.putString("lname", result.lname)
        editor.putString("dob", result.dob)
        editor.putString("email_id", result.emailId)
        editor.putString("code", result.code)
        editor.putString("mobile_no", result.mobileNo)
        editor.putString("user_type", result.userType)
        editor.putString("img", result.img)
        editor.putString("en_time", result.enTime)
        editor.putFloat("AcBal", result.acBal.toFloat())
        editor.putString("AcChipsBal", result.acChipsBal)
        editor.putString("onlineUser", result.onlineUser)
        editor.putString("login_st", result.loginSt)
        editor.putString("sid", result.sid)
        editor.putString("pass_key", passKey)

        editor.apply()

    }

    val keyModel: KeyModel
        get() {
            val sPref = mCtx.getSharedPreferences(SHARED_PREF_KEY_MODEL, Context.MODE_PRIVATE)
            return KeyModel(
                sPref.getString("gameRequestId", "").toString(),
                sPref.getInt("gameType", 0),
                sPref.getFloat("amount", 0f),
                sPref.getInt("ticketType", 0),
                sPref.getString("tournamentId", "").toString()
            )
        }

    fun keyModel(keyModel: KeyModel){
        val sPref = mCtx.getSharedPreferences(SHARED_PREF_KEY_MODEL, Context.MODE_PRIVATE)
        val editor = sPref.edit()

        editor.putInt("gameType", keyModel.gameType)
        editor.putFloat("amount", keyModel.amount)
        editor.putInt("ticketType", keyModel.ticketType)
        editor.putString("tournamentId", keyModel.tournamentId)

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
        private val SHARED_PREF_KEY_MODEL = "key_model_shared_preff"
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