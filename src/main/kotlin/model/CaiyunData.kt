package org.laolittle.plugin.caiyun.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
data class PhoneMessage(
    @SerialName("codeid") val codeId: String
)

@Serializable
data class UserData(
    @SerialName("_id") val uid: String,
    @SerialName("nickname") val nickname: String
)

@Serializable
data class UserInfoData(
    @SerialName("data") val data: JsonElement
)

@Serializable
data class CaiyunStatus(
    @SerialName("status") val status: String,
    @SerialName("msg") val message: String?,
)

internal val Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}