package model.paytm


import com.google.gson.annotations.SerializedName

data class ResPaytmOrderChecksum(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("Result")
    var result: List<Result>,
    @SerializedName("status")
    var status: Int
)