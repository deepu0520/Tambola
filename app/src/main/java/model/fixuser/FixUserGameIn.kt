package model.fixuser


import com.google.gson.annotations.SerializedName
import model.gamein.GameIn
import java.io.Serializable

data class FixUserGameIn(
    @SerializedName("FixUsersGameIn")
    var fixUsersGameIn: List<GameIn>
): Serializable