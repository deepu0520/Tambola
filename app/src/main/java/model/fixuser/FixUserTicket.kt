package model.fixuser


import com.google.gson.annotations.SerializedName
import model.NumModel
import model.claim.PrizeModel
import model.gamein.GameIn
import java.io.Serializable

data class FixUserTicket(
    @SerializedName("ticketNumbers")
    var ticketNumbers: List<NumModel>,
    @SerializedName("topLine")
    var topLine: Boolean,
    @SerializedName("midLine")
    var midLine: Boolean,
    @SerializedName("bottomLine")
    var bottomLine: Boolean,
    @SerializedName("forCorners")
    var forCorners: Boolean,
    @SerializedName("earlyFive")
    var earlyFive: Boolean,
    @SerializedName("fullHouse")
    var fullHouse: Boolean
): Serializable