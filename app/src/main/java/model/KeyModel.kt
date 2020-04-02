package model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class KeyModel (
	@SerializedName("gameRequestId") var gameRequestId : String,
	@SerializedName("gameType") var gameType : Int,
	@SerializedName("amount") var amount : Float,
	@SerializedName("ticketType") var ticketType : Int,
	@SerializedName("tournamentId") var tournamentId : String
): Serializable