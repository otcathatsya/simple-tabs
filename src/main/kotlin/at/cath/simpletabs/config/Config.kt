package at.cath.simpletabs.config

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@kotlinx.serialization.Serializable
class Config(@EncodeDefault val deeplApiKey: String = "")