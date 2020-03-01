package model.login

import com.google.gson.annotations.SerializedName

data class Result (

	@SerializedName("id") val id : String,
	@SerializedName("inactive") val inactive : Int,
	@SerializedName("fname") val fname : String,
	@SerializedName("lname") val lname : String,
	@SerializedName("dob") val dob : String,
	@SerializedName("email_id") val email_id : String,
	@SerializedName("code") val code : String,
	@SerializedName("mobile_no") val mobile_no : String,
	@SerializedName("user_type") val user_type : String,
	@SerializedName("img") val img : String,
	@SerializedName("en_time") val en_time : String,
	@SerializedName("AcBal") val acBal : Float,
	@SerializedName("onlineUser") val onlineUser : Int,
	@SerializedName("login_st") val login_st : Int,
	@SerializedName("sid") val sid : String
)