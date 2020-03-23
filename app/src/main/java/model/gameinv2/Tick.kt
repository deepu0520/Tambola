package model.gameinv2


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Tick(
    @SerializedName("ticket1")
    var ticket1: String
) : Serializable