package model.claim


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("amt")
    var amt: String,
    @SerializedName("claim_time")
    var claimTime: String,
    @SerializedName("claimType")
    var claimType: String,
    @SerializedName("game_type")
    var gameType: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("tournament_id")
    var tournamentId: String,
    @SerializedName("user_id")
    var userId: String,
    @SerializedName("user_nm")
    var userNm: String
): Serializable