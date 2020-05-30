package model.login


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("id")
    var id: String,
    @SerializedName("inactive")
    var inactive: String,
    @SerializedName("fname")
    var fname: String,
    @SerializedName("lname")
    var lname: String,
    @SerializedName("dob")
    var dob: String,
    @SerializedName("email_id")
    var emailId: String,
    @SerializedName("code")
    var code: String,
    @SerializedName("mobile_no")
    var mobileNo: String,
    @SerializedName("user_type")
    var userType: String,
    @SerializedName("img")
    var img: String,
    @SerializedName("fbImg")
    var fbImg: String,
    @SerializedName("en_time")
    var enTime: String,
    @SerializedName("AcBal")
    var acBal: String,
    @SerializedName("AcChipsBal")
    var acChipsBal: String,
    @SerializedName("login_st")
    var loginSt: String,
    @SerializedName("onlineUser")
    var onlineUser: String,
    @SerializedName("sid")
    var sid: String,
    @SerializedName("isLogin")
    var isLogin: Boolean = false
): Serializable