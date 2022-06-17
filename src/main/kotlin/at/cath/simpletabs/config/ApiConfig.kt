package at.cath.simpletabs.config

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@kotlinx.serialization.Serializable
class ApiConfig(@EncodeDefault var deeplApiKey: String = "") {
    fun setApiKey(apiKey: String): ApiConfig = apply { this.deeplApiKey = apiKey }
}