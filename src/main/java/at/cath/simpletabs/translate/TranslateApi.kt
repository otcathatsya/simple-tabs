package at.cath.simpletabs.translate

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface TranslateApi {
    @GET("translate")
    suspend fun getTranslation(
        @Query("text") sourceText: String,
        @Query("target_lang") targetLanguage: String
    ): Response<TranslationResult>
}