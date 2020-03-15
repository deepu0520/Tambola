package retrofit

import com.google.gson.GsonBuilder
import com.newitzone.tambola.utils.Constants
import model.DefaultResponse
import model.gamestatus.ResGameStatus
import model.login.ResLogin
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


interface TambolaApiService {

    /**
     * Companion object to create the TambolaApiService
     */

    @FormUrlEncoded
    @POST("game-request-status")
    suspend fun gameRequestStatus(@Field("userid") userid: String,
                                  @Field("sesid") sesid: String,
                                  @Field("game_type") game_type: String,
                                  @Field("amt") amt: String,
                                  @Field("tournament_id") tournament_id: String
    ): Response<ResGameStatus>

    @FormUrlEncoded
    @POST("game-request")
    suspend fun gameRequest(@Field("userid") userid: String,
                            @Field("sesid") sesid: String,
                            @Field("amt") amt: String,
                            @Field("req_ticket") req_ticket: String,
                            @Field("game_type") game_type: String,
                            @Field("tournament_id") tournament_id: String
    ): Response<DefaultResponse>

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
                         @Field("userType") userType: String,
                         @Field("loginType") loginType: String,
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

        private fun okHttpBuilder(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build()
        }

        fun makeRetrofitService(): TambolaApiService {
            return Retrofit.Builder()
                .baseUrl(Constants.API_BASE_PATH)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(okHttpBuilder())
                .build().create(TambolaApiService::class.java)
        }
    }
}