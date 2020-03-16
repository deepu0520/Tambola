package model.gamein


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("amt")
    val amt: String,
    @SerializedName("game_id")
    val gameId: String,
    @SerializedName("game_type")
    val gameType: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("rand_no")
    val randNo: List<Int>,
    @SerializedName("req_status")
    val reqStatus: String,
    @SerializedName("tick")
    val tick: Tick,
    @SerializedName("ticket_count")
    val ticketCount: String,
    @SerializedName("tournament_id")
    val tournamentId: String,
    @SerializedName("user_id")
    val userId: String
) : Serializable