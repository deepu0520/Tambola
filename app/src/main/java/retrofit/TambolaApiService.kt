package retrofit

import com.newitzone.tambola.utils.Constants
import model.LoginResponse
import model.login.ResLogin
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface TambolaApiService {

    /**
     * Companion object to create the TambolaApiService
     */
//    @GET("/posts")
//    suspend fun getPosts(): Response<List<POST>>
    @GET("login")
    suspend fun getlogin(@Query("userName") userName:String,
                         @Query("passkey") passkey: String,
                         @Query("userType") userType: Int,
                         @Query("loginType") loginType: Int,
                         @Query("sesID") sesID: String,
                         @Query("userID") userID: String
    ): Response<LoginResponse>

    object RetrofitFactory {
        const val BASE_URL = "https://api.newitzone.com/tambola/"//"https://jsonplaceholder.typicode.com"
        fun makeRetrofitService(): TambolaApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build().create(TambolaApiService::class.java)
        }
    }
}