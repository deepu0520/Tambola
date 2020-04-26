package model.paytm


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("checkSum")
    var checkSum: String,
    @SerializedName("ordid")
    var ordid: String,
    @SerializedName("ordno")
    var ordno: String
)