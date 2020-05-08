package model.paytm


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("mid")
    var mId: String,
    @SerializedName("ind_typ_id")
    var industryTypeId: String,
    @SerializedName("chnl_id")
    var channelId: String,
    @SerializedName("website")
    var website: String,
    @SerializedName("callbk_url")
    var callBackUrl: String,
    @SerializedName("m_key")
    var mKey: String,
    @SerializedName("checkSum")
    var checkSum: String,
    @SerializedName("ordid")
    var ordid: String,
    @SerializedName("ordno")
    var ordno: String
) : Serializable