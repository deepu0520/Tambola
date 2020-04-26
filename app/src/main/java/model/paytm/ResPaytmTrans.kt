package model.paytm

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ResPaytmTrans(
    @SerializedName("BANKTXNID")
    var bANKTXNID: String,
    @SerializedName("CHECKSUMHASH")
    var cHECKSUMHASH: String,
    @SerializedName("CURRENCY")
    var cURRENCY: String,
    @SerializedName("MID")
    var mID: String,
    @SerializedName("ORDERID")
    var oRDERID: String,
    @SerializedName("RESPCODE")
    var rESPCODE: String,
    @SerializedName("RESPMSG")
    var rESPMSG: String,
    @SerializedName("STATUS")
    var sTATUS: String,
    @SerializedName("TXNAMOUNT")
    var tXNAMOUNT: String,
    @SerializedName("BANKNAME")
    var bANKNAME: String,
    @SerializedName("TXNDATE")
    var tXNDATE: String,
    @SerializedName("TXNID")
    var tXNID: String,
    @SerializedName("PAYMENTMODE")
    var pAYMENTMODE: String,
    @SerializedName("GATEWAYNAME")
    var gATEWAYNAME: String
) : Serializable