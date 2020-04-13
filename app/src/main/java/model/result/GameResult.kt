package model.result


import com.google.gson.annotations.SerializedName
import model.NumModel
import model.claim.PrizeModel
import model.gamein.GameIn
import java.io.Serializable

data class GameResult(
    @SerializedName("result")
    var resultList: List<Result>
): Serializable