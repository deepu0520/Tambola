package model.gamestatus


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResGameStatus(
    @SerializedName("msg")
    val msg: String,
    @SerializedName("Result")
    val result: List<Result>,
    @SerializedName("status")
    val status: Int
): Serializable