package model.tournament.start


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResTournamentStart(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("Result")
    var result: List<Result>,
    @SerializedName("status")
    var status: Int
) : Serializable