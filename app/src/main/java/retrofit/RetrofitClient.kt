package retrofit

import com.newitzone.tambola.utils.Constants
import okhttp3.OkHttpClient
import retrofit.Api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->

                val request = chain.request()
                        .newBuilder()
                        .addHeader("Connection", "close")
                        .build()
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