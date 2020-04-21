package model.tournament


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Result(
    @SerializedName("result_show_after")
    var resultShowAfter: Int,
    @SerializedName("date")
    var date: String,
    @SerializedName("day")
    var day: String,
    @SerializedName("list")
    var list: List<Tournament>
) : Serializable