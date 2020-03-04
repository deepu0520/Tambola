package model.login


import com.google.gson.annotations.SerializedName

data class ResLogin(
    @SerializedName("msg")
    val msg: String,
    @SerializedName("Result")
    val result: Result,
    @SerializedName("status")
    val status: Int
)