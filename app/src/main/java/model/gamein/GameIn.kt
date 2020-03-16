package model.gamein


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GameIn(
    @SerializedName("msg")
    val msg: String,
    @SerializedName("Result")
    val result: List<Result>,
    @SerializedName("status")
    val status: Int
) : Serializable