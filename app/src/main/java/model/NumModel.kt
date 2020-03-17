package model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NumModel (
	@SerializedName("num") var num : Int,
	@SerializedName("isChecked") var isChecked : Boolean
): Serializable