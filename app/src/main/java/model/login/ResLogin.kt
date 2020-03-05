package model.login


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResLogin(
    @SerializedName("msg")
    val msg: String,
    @SerializedName("Result")
    val result: Result,
    @SerializedName("status")
    val status: Int
): Serializable