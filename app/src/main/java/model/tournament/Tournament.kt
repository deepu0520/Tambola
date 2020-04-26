package model.tournament

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Tournament(
    @SerializedName("amount")
    var amount: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("max_ticket")
    var maxTicket: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("request_open")
    var requestOpen: String,
    @SerializedName("starttime")
    var startTime: String,
    @SerializedName("total_Requested_ticket")
    var totalRequestedTicket: String,
    @SerializedName("user_requested_tickets")
    var userRequestedTickets: String,
    @SerializedName("req_id")
    var req_id: String,
    @SerializedName("game_id")
    var game_id: String,
    @SerializedName("timer")
    var timer: Boolean = false
) : Serializable
