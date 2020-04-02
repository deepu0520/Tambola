package model.gamein


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Tick(
    @SerializedName("ticket1")
    var ticket1: List<List<Int>>,
    @SerializedName("ticket2")
    var ticket2: List<List<Int>>
) : Serializable