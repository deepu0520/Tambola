import com.google.gson.annotations.SerializedName

data class ResLogin (

	@SerializedName("status") val status : Int,
	@SerializedName("msg") val msg : String,
	@SerializedName("Result") val result : Result
)