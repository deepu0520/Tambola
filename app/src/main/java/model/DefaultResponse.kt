package model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DefaultResponse (

	@SerializedName("status") val status : Int,
	@SerializedName("msg") val msg : String,
	@SerializedName("Result") val result : List<String>
): Serializable