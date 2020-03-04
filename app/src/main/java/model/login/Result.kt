package model.login


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("id")
    val id: String,
    @SerializedName("inactive")
    val inactive: String,
    @SerializedName("fname")
    val fname: String,
    @SerializedName("lname")
    val lname: String,
    @SerializedName("dob")
    val dob: Any,
    @SerializedName("email_id")
    val emailId: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("mobile_no")
    val mobileNo: String,
    @SerializedName("user_type")
    val userType: String,
    @SerializedName("img")
    val img: String,
    @SerializedName("en_time")
    val enTime: String,
    @SerializedName("AcBal")
    val acBal: Float,
    @SerializedName("onlineUser")
    val onlineUser: String,
    @SerializedName("login_st")
    val loginSt: String,
    @SerializedName("sid")
    val sid: String
)