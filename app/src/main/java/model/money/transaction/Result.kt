package model.money.transaction


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("amt")
    var amt: String,
    @SerializedName("doc_no")
    var docNo: String,
    @SerializedName("entry_datetime")
    var entryDatetime: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("payment_getway_resp")
    var paymentGetwayResp: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("txn_status")
    var txnStatus: String
)