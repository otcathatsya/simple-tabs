package at.cath.simpletabs.translate

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class TranslationResult(
    val translations: List<Translation>
) {

    @kotlinx.serialization.Serializable
    class Translation(
        @SerialName("text") val translatedText: String,
        @SerialName("detected_source_language") val sourceLanguage: String
    )
}