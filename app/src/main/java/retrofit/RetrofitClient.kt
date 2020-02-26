package simplifiedcoding.net.kotlinretrofittutorial.api

import android.util.Base64
import com.newitzone.tambola.utils.Constants
import okhttp3.OkHttpClient
import retrofit.Api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()

                val requestBuilder = original.newBuilder()
                        .addHeader("Authorization", Constants.API_AUTHORIZATION_KEY)
                        .method(original.method(), original.body())

                val request = requestBuilder.build()
                chain.proceed(request)
            }.build()

    val instance: Api by lazy{
        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.API_BASE_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()

        retrofit.create(Api::class.java)
    }

}