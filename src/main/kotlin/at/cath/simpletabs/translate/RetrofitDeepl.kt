package at.cath.simpletabs.translate

import at.cath.simpletabs.config.ConfigManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@OptIn(ExperimentalSerializationApi::class)
object RetrofitDeepl {
    val api: TranslateApi by lazy {
        val apiKey = ConfigManager.config.deeplApiKey
        if (apiKey.isEmpty()) throw IllegalArgumentException("Api key must be provided in config to use translations")
        createServices(apiKey)
    }

    private fun createServices(apiKey: String): TranslateApi {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                val originalReq = it.request()
                val url = originalReq.url.newBuilder().addQueryParameter("auth_key", apiKey).build()
                it.proceed(originalReq.newBuilder().url(url).build())
            }.addInterceptor(interceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl("https://api-free.deepl.com/v2/")
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TranslateApi::class.java)
    }
}