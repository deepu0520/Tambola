package model.gamein


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("game_id")
    var gameId: String,
    @SerializedName("game_value")
    var gameValue: List<GameValue>,
    @SerializedName("rand_no")
    var randNo: List<Int>,
    @SerializedName("tournament_id")
    var tournamentId: String,
    @SerializedName("user_data")
    var userData: List<UserData>
) : Serializable