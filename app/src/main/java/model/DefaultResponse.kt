package model

import com.google.gson.annotations.SerializedName
data class DefaultResponse (

	@SerializedName("status") val status : Int,
	@SerializedName("msg") val msg : String,
	@SerializedName("model.login.Result") val result : List<String>
)