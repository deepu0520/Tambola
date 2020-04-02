package model.fixuser


import com.google.gson.annotations.SerializedName

data class ResFixUser(
    @SerializedName("FixUsers")
    var fixUsers: List<FixUser>
)