package model.gamerequest


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("RequestID")
    var requestID: String
)