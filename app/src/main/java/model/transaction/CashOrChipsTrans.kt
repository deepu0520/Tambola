package model.transaction


import com.google.gson.annotations.SerializedName

data class CashOrChipsTrans(
    @SerializedName("msg")
    var msg: String,
    @SerializedName("Result")
    var result: List<List<Result>>,
    @SerializedName("status")
    var status: Int
)