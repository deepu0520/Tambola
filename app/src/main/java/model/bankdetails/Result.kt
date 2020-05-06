package model.bankdetails


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("bank_name")
    var bankName: String,
    @SerializedName("ifcs_code")
    var ifcsCode: String,
    @SerializedName("account_no")
    var accountNo: String,
    @SerializedName("account_holder")
    var accountHolder: String
) : Serializable