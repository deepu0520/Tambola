package com.newitzone.desitambola.utils;

import android.content.Context;
import android.content.SharedPreferences;

import model.login.Result;

public class DesiTambolaPreferences {

    public static final String LOGIN_PREFERENCE = "desi_tambola_login_pref";
    public static final String PASS_KEY_PREFERENCE = "desi_tambola_paas_key_pref";
    
    public static final String ID = "id";
    public static final String INACTIVE = "inactive";
    public static final String FIRST_NAME = "fname";
    public static final String LAST_NAME = "lname";
    public static final String DOB = "dob";
    public static final String EMAIL = "email_id";
    public static final String CODE = "code";
    public static final String MOBILE = "mobile_no";
    public static final String USER_TYPE = "user_type";
    public static final String IMAGE = "img";
    public static final String FB_IMAGE = "fbImg";
    public static final String EN_TIME = "en_time";
    public static final String ACCOUNT_BALANCE = "AcBal";
    public static final String ACCOUNT_CHIPS_BALANCE = "AcChipsBal";
    public static final String LOGIN_STATUS = "login_st";
    public static final String ONLINE_USER = "onlineUser";
    public static final String SESSION_ID = "sid";
    public static final String IS_LOGIN = "isLogin";

    public static final String PASS_KEY = "passKey";

    public static void setLogin(Context context, Result login){
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ID, login.getId());
        editor.putString(INACTIVE, login.getInactive());
        editor.putString(FIRST_NAME,login.getFname());
        editor.putString(LAST_NAME,login.getLname());
        editor.putString(DOB, login.getDob() == null? "": login.getDob());
        editor.putString(EMAIL, login.getEmailId());
        editor.putString(CODE, login.getCode());
        editor.putString(MOBILE, login.getMobileNo());
        editor.putString(USER_TYPE, login.getUserType());
        editor.putString(IMAGE, login.getImg());
        editor.putString(FB_IMAGE, login.getFbImg());
        editor.putString(EN_TIME, login.getEnTime());
        editor.putString(ACCOUNT_BALANCE, login.getAcBal());
        editor.putString(ACCOUNT_CHIPS_BALANCE, login.getAcChipsBal());
        editor.putString(LOGIN_STATUS, login.getLoginSt());
        editor.putString(ONLINE_USER, login.getOnlineUser());
        editor.putString(SESSION_ID, login.getSid());
        editor.putBoolean(IS_LOGIN, true);
        // commit
        editor.commit();
    }

    public static Result getLogin(Context context) {
        SharedPreferences pref = context.getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        Result login = new Result(
                pref.getString(ID, ""),
                pref.getString(INACTIVE, ""),
                pref.getString(FIRST_NAME, ""),
                pref.getString(LAST_NAME, ""),
                pref.getString(DOB, ""),
                pref.getString(EMAIL, ""),
                pref.getString(CODE, ""),
                pref.getString(MOBILE, ""),
                pref.getString(USER_TYPE, ""),
                pref.getString(IMAGE,""),
                pref.getString(FB_IMAGE,""),
                pref.getString(EN_TIME,""),
                pref.getString(ACCOUNT_BALANCE,""),
                pref.getString(ACCOUNT_CHIPS_BALANCE,""),
                pref.getString(LOGIN_STATUS,""),
                pref.getString(ONLINE_USER,""),
                pref.getString(SESSION_ID,""),
                pref.getBoolean(IS_LOGIN,false)
        );
        return login;
    }
    public static void logout(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(LOGIN_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        // commit
        editor.commit();
    }
    public static void setPassKey(Context context, String passKey) {
        SharedPreferences prefs = context.getSharedPreferences(PASS_KEY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PASS_KEY, passKey);
        // commit
        editor.commit();
    }
    public static String getPassKey(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PASS_KEY_PREFERENCE, Context.MODE_PRIVATE);
        return prefs.getString(PASS_KEY, "");
    }
    public static void passKeyClear(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PASS_KEY_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        // commit
        editor.commit();
    }
}
