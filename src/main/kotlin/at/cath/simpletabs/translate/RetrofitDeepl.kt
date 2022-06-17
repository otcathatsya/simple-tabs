package at.cath.simpletabs.translate

import at.cath.simpletabs.config.ConfigManager
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalSerializationApi::class)
object RetrofitDeepl {

    private const val BASE_URL = "https://api-free.deepl.com/v2/"

    val api: TranslateApi by lazy {
        val apiKey = ConfigManager.apiConfig.deeplApiKey
        if (apiKey.isEmpty()) throw IllegalArgumentException("Api key must be provided in config to use translations")
        createServices(apiKey)
    }

    fun verifyKey(apiKey: String): Boolean {
        val client = OkHttpClient.Builder().connectTimeout(2, TimeUnit.SECONDS).build()
        val httpUrl = HttpUrl.Builder()
            .scheme("https")
            .host("api-free.deepl.com")
            .addPathSegment("v2")
            .addPathSegment("usage")
            .addQueryParameter("auth_key", apiKey).build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .build()

        client.newCall(request).execute().use { response -> return response.code == 200 }
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
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TranslateApi::class.java)
    }
}