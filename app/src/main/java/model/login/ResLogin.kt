package model.login

import com.google.gson.annotations.SerializedName

data class ResLogin (

	@SerializedName("status") val status : Int,
	@SerializedName("msg") val msg : String,
	@SerializedName("model.login.Result") val result : Result
)