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
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@OptIn(ExperimentalSerializationApi::class)
object RetrofitDeepl {

    private const val BASE_URL = "https://api-free.deepl.com/v2/"
    private var sslContext: SSLContext = SSLContext.getInstance("SSL")

    private val TRUST_ALL_CERTS: TrustManager = object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }

    init {
        sslContext.init(null, arrayOf(TRUST_ALL_CERTS), SecureRandom())
    }

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
            .sslSocketFactory(sslContext.socketFactory, TRUST_ALL_CERTS as X509TrustManager)
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