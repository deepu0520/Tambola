package model.tournament.start


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("game_id")
    var gameId: String,
    @SerializedName("game_type")
    var gameType: String,
    @SerializedName("game_value")
    var gameValue: List<GameValue>,
    @SerializedName("id")
    var id: String,
    @SerializedName("rand_no")
    var randNo: List<Int>,
    @SerializedName("req_time")
    var reqTime: String,
    @SerializedName("ticket_count")
    var ticketCount: String,
    @SerializedName("tickets")
    var tickets: Tickets,
    @SerializedName("tournament_id")
    var tournamentId: String,
    @SerializedName("user_id")
    var userId: String
) : Serializable