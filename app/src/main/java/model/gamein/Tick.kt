package model.gamein


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Tick(
    @SerializedName("ticket1")
    val ticket1: List<Any>,
    @SerializedName("ticket2")
    val ticket2: List<Any>
) : Serializable