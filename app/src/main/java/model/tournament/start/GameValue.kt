package model.tournament.start


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GameValue(
    @SerializedName("bot_line")
    var botLine: Double,
    @SerializedName("early_five")
    var earlyFive: Double,
    @SerializedName("four_cor")
    var fourCor: Double,
    @SerializedName("full_house")
    var fullHouse: Int,
    @SerializedName("mid_line")
    var midLine: Double,
    @SerializedName("top_line")
    var topLine: Double
) : Serializable