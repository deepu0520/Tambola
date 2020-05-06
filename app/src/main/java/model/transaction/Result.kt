package model.transaction


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("amt")
    var amt: String,
    @SerializedName("descrption")
    var descrption: String,
    @SerializedName("doc_no")
    var docNo: String,
    @SerializedName("trans_dt")
    var transDt: String,
    @SerializedName("TransType")
    var transType: String
)