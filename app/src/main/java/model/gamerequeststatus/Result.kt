package model.gamerequeststatus


import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("game_id")
    var gameId: String,
    @SerializedName("players_online")
    var playersOnline: String,
    @SerializedName("rand_no")
    var randNo: String,
    @SerializedName("total_tickets")
    var totalTickets: String,
    @SerializedName("tournament_id")
    var tournamentId: String,
    @SerializedName("user_data")
    var userData: List<Any>
)