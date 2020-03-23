package model.gameinv2


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GameInV2(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("Result")
    var result: Result,
    @SerializedName("status")
    var status: Int
) : Serializable