package model

data class LoginResponse(
    val Result: Result,
    val msg: String,
    val status: Int
)