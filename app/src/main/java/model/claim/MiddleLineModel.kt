package model.claim

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MiddleLineModel (
	@SerializedName("num") var num : Int,
	@SerializedName("position") var position : Int,
	@SerializedName("isChecked") var isChecked : Boolean
): Serializable