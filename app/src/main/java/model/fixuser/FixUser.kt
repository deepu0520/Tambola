package model.fixuser


import com.google.gson.annotations.SerializedName

data class FixUser(
    @SerializedName("amt")
    var amt: Int,
    @SerializedName("gameType")
    var gameType: Int,
    @SerializedName("passKey")
    var passKey: String,
    @SerializedName("sessionId")
    var sessionId: String,
    @SerializedName("ticketReq")
    var ticketReq: Int,
    @SerializedName("tournamentId")
    var tournamentId: String,
    @SerializedName("userId")
    var userId: String,
    @SerializedName("userName")
    var userName: String
)