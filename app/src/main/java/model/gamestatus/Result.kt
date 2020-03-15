package model.gamestatus


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("players_online")
    val playersOnline: Int
): Serializable