package org.laolittle.plugin.caiyun.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
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
data class Data(
    @SerialName("data") val data: JsonElement
)

@Serializable
data class FirstNode(
    @SerialName("firstnode") val firstNode: JsonElement
)

@Serializable
data class NodeId(
    @SerialName("nid") val nid: String,
    @SerialName("nodeid") val nodeId: String
)

@Serializable
data class Novel(
    @SerialName("nodeid") val nodeId: String,
    @SerialName("content") val content: String
)

@Serializable
data class Nodes(
    @SerialName("nodes") val nodes: JsonArray
)

@Serializable
data class CaiyunStatus(
    @SerialName("status") val status: String,
    @SerialName("msg") val message: String,
)

internal val Json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}