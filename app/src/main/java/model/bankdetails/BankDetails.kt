package model.bankdetails


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class BankDetails(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("Result")
    var result: List<List<Result>>,
    @SerializedName("status")
    var status: Int
) : Serializable