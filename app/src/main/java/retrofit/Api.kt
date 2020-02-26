package retrofit

import model.DefaultResponse
import model.login.ResLogin
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Api {

    @FormUrlEncoded
    @POST("registration")
    fun registration(
            @Field("fname") fname:String,
            @Field("lname") lname:String,
            @Field("emailID") emailID:String,
            @Field("mobileNo") mobileNo:String,
            @Field("passkey") passkey:String,
            @Field("dob") dob:String,
            @Field("userType") userType:String,
            @Field("img") img:String

    ):Call<DefaultResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
            @Field("userName") userName:String,
            @Field("passkey") passkey: String,
            @Field("userType") userType: Int,
            @Field("loginType") loginType: Int,
            @Field("sesID") sesID: String,
            @Field("userID") userID: String
    ):Call<ResLogin>

    @FormUrlEncoded
    @POST("profile-update")
    fun profileUpdate(@Field("fname") fname: String,
                      @Field("lname") lname: String,
                      @Field("mobileNo") mobileNo: String,
                      @Field("passkey") passkey: String,
                      @Field("userid") userid: String,
                      @Field("sesid") sesid: Int,
                      @Field("dob") dob: String,
                      @Field("img") img: String
    ): Call<DefaultResponse>
}