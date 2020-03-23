package model.gameinv2


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("game_id")
    var gameId: String,
    @SerializedName("rand_no")
    var randNo: String,
    @SerializedName("tournament_id")
    var tournamentId: String,
    @SerializedName("user_data")
    var userData: List<UserData>
) : Serializable