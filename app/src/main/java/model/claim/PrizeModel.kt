package model.claim

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PrizeModel (
	@SerializedName("id") var id : Int,
	@SerializedName("name") var name : String,
	@SerializedName("userId") var userId : String,
	@SerializedName("userName") var userName : String,
	@SerializedName("isChecked") var isChecked : Boolean
): Serializable