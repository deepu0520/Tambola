package model

data class DefResponse(
    val Result: List<Any>,
    val msg: String,
    val status: Int
)