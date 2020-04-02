package model.gamein
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GameValue(
    @SerializedName("top_line") val topLine : Double,
    @SerializedName("bot_line") val botLine : Double,
    @SerializedName("mid_line") val midLine : Double,
    @SerializedName("full_house") val fullHouse : Double,
    @SerializedName("four_cor") val fourCor : Double,
    @SerializedName("early_five") val earlyFive : Double
) : Serializable