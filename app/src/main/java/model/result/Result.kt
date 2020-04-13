package model.result


import com.google.gson.annotations.SerializedName
import model.NumModel
import model.claim.PrizeModel
import model.gamein.GameIn
import java.io.Serializable

data class Result(
    @SerializedName("serialNo")
    var serialNo: Int,
    @SerializedName("userId")
    var userId: String,
    @SerializedName("userName")
    var userName: String,
    @SerializedName("prize")
    var prize: String,
    @SerializedName("amount")
    var amount: Double,
    @SerializedName("gameType")
    var gameType: Int
): Serializable