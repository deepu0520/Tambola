package retrofit

import ResLogin
import io.reactivex.Observable
import Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

interface TambolaApiService {

    /**
     * Companion object to create the TambolaApiService
     */
    companion object Factory {
        fun create(): TambolaApiService {
            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.newitzone.com/tambola/")
                .build()
            return retrofit.create(TambolaApiService::class.java);
        }
    }


    // Api's
    // TODO: Registration
    @POST("registration")
    fun registration(@Query("fname") fname: String,
                     @Query("lname") lname: String,
                     @Query("emailID") emailID: String,
                     @Query("mobileNo") mobileNo: String,
                     @Query("passkey") passkey: String,
                     @Query("dob") dob: String,
                     @Query("userType") userType: Int,
                     @Query("img") img: Int
    ): Observable<Response>
    // TODO: Login
    @POST("login")
    fun login(@Query("userName") userName: String,
              @Query("passkey") passkey: String,
              @Query("userType") userType: Int,
              @Query("loginType") loginType: Int,
              @Query("sesID") sesID: String,
              @Query("userID") userID: String
    ): Observable<ResLogin>


    // TODO: profile-update
    @POST("profile-update")
    fun profileUpdate(@Query("fname") fname: String,
                      @Query("lname") lname: String,
                      @Query("mobileNo") mobileNo: String,
                      @Query("passkey") passkey: String,
                      @Query("userid") userid: String,
                      @Query("sesid") sesid: Int,
                      @Query("dob") dob: String,
                      @Query("img") img: String
    ): Observable<Response>
}