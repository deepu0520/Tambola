package model.fixuser


import com.google.gson.annotations.SerializedName
import model.NumModel
import model.gamein.GameIn
import java.io.Serializable
data class FixUserAutoPlay(
    @SerializedName("fixUser")
    var fixUser: FixUser,
    @SerializedName("ticket1")
    var ticket1: FixUserTicket,
    @SerializedName("ticket2")
    var ticket2: FixUserTicket
): Serializable