package retrofit

import com.newitzone.tambola.utils.Constants
import model.DefaultResponse
import model.login.ResLogin
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface TambolaApiService {

    /**
     * Companion object to create the TambolaApiService
     */

    @FormUrlEncoded
    @POST("profile-update")
    suspend fun updateProfile(@Field("fname") fname: String,
                              @Field("lname") lname: String,
                              @Field("mobileNo") mobileNo: String,
                              @Field("passkey") passkey: String,
                              @Field("userid") userid: String,
                              @Field("sesid") sesid: String,
                              @Field("dob") dob: String,
                              @Field("img") img: String
    ): Response<DefaultResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun getlogin(@Field("userName") userName:String,
                         @Field("passkey") passkey: String,
                         @Field("userType") userType: Int,
                         @Field("loginType") loginType: Int,
                         @Field("sesID") sesID: String,
                         @Field("userID") userID: String
    ): Response<ResLogin>

    @FormUrlEncoded
    @POST("registration")
    suspend fun registration(
        @Field("fname") fname:String,
        @Field("lname") lname:String,
        @Field("emailID") emailID:String,
        @Field("mobileNo") mobileNo:String,
        @Field("passkey") passkey:String,
        @Field("dob") dob:String,
        @Field("userType") userType:String,
        @Field("img") img:String
    ): Response<DefaultResponse>


    object RetrofitFactory {
        fun makeRetrofitService(): TambolaApiService {
            return Retrofit.Builder()
                .baseUrl(Constants.API_BASE_PATH)
                .addConverterFactory(MoshiConverterFactory.create())
                .build().create(TambolaApiService::class.java)
        }
    }
}