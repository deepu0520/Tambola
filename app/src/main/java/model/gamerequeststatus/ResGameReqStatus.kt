package model.gamerequeststatus


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResGameReqStatus(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("Result")
    var result: List<Result>,
    @SerializedName("status")
    var status: Int
) :  Serializable