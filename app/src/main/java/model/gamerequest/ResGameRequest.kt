package model.gamerequest


import com.google.gson.annotations.SerializedName

data class ResGameRequest(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("Result")
    var result: List<Result>,
    @SerializedName("status")
    var status: Int
)