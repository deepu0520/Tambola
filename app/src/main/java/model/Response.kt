import com.google.gson.annotations.SerializedName
data class Response (

	@SerializedName("status") val status : Int,
	@SerializedName("msg") val msg : String,
	@SerializedName("Result") val result : List<String>
)