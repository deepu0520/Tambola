package model.tournament


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResTournament(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("Result")
    var result: Result,
    @SerializedName("status")
    var status: Int
) : Serializable