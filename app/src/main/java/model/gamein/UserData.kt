package model.gamein


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserData(
    @SerializedName("amt")
    var amt: String,
    @SerializedName("game_type")
    var gameType: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("req_status")
    var reqStatus: String,
    @SerializedName("tick")
    var tick: Tick,
    @SerializedName("ticket_count")
    var ticketCount: String,
    @SerializedName("user_id")
    var userId: String
): Serializable