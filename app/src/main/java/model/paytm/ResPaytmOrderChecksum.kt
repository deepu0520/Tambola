package model.paytm


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResPaytmOrderChecksum(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("Result")
    var result: List<Result>,
    @SerializedName("status")
    var status: Int
) : Serializable