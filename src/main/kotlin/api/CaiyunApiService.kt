package org.laolittle.plugin.caiyun.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import org.laolittle.plugin.caiyun.Config.modelId
import org.laolittle.plugin.caiyun.Config.userId
import org.laolittle.plugin.caiyun.model.*
import org.laolittle.plugin.caiyun.utils.KtorOkHttp.get
import org.laolittle.plugin.caiyun.utils.KtorOkHttp.post

@ExperimentalSerializationApi
object CaiyunApiService {

    private const val BASE_URL = "https://if.caiyunai.com/v2"

    suspend fun getModels(): JsonArray {
        val jsonStr = get("$BASE_URL/model/model_list")
        val data = Json.decodeFromJsonElement<Data>(jsonStr).data
        return Json.decodeFromJsonElement<Map<String, JsonArray>>(data)["models"]!!
    }
    
    suspend fun startWrite(title: String, text: String, nodeId: String, novelId: String): Novel {
        val jsonRequest = buildJsonObject {
            put("content", text)
            put("lang", "zh")
            put("lastnode", nodeId)
            put("mid", modelId)
            put("nid", novelId)
            put("ostype", "")
            put("status", "http")
            put("storyline", false)
            put("title", title)
            put("uid", userId)
        }

        val json = jsonRequest.toString().post("$BASE_URL/novel/$userId/novel_ai")
        val data = Json.decodeFromJsonElement<Data>(json).data
        val nodes = Json.decodeFromJsonElement<Nodes>(data).nodes
        return Json.decodeFromJsonElement(nodes[0])
    }

    suspend fun getNovelInfo(title: String, text: String, nodeId: Boolean, putNodes: JsonArray = buildJsonArray { }): String {
        val jsonRequest = buildJsonObject {
            put("lang", "zh")
            put("nodes", putNodes)
            put("ostype", "")
            put("text", text)
            put("title", title)
        }

        val json = jsonRequest.toString().post("$BASE_URL/novel/$userId/novel_save")
        val data = Json.decodeFromJsonElement<Data>(json).data
        val firstNode = Json.decodeFromJsonElement<FirstNode>(data).firstNode
        val n: NodeId = Json.decodeFromJsonElement(firstNode)
        return if (nodeId) n.nodeId else n.nid
        /*
        val firstNode: FirstNode = Json.decodeFromJsonElement(data)
        val nodeId = Json.decodeFromJsonElement<NodeId>(firstNode.node).nodeId
        */

    }

    suspend fun sendVerification(PhoneNumber: Long): String {
        val jsonRequest = buildJsonObject {
            put("type", "login")
            put("phone", PhoneNumber)
            put("callcode", 86)
            put("uid", "")
            put("lang", "zh")
            put("ostype", "")
        }

        val json = jsonRequest.toString().post("$BASE_URL/user/phone_message")
        val returnData: Data
        val code: PhoneMessage
        try {
            returnData = Json.decodeFromJsonElement(json)
            code = Json.decodeFromJsonElement(returnData.data)
        } catch (e: Exception) {
            val caiyunStatus: CaiyunStatus = Json.decodeFromJsonElement(json)
            return caiyunStatus.message
        }
        return code.codeId
    }

    suspend fun loginFromCode(codeId: String, code: Int, PhoneNumber: Long): String {
        val jsonRequest = buildJsonObject {
            put("code", code)
            put("phone", PhoneNumber)
            put("codeid", codeId)
            put("uid", "")
            put("callcode", 86)
            put("lang", "zh")
            put("ostype", "")
        }

        val json = jsonRequest.toString().post("$BASE_URL/user/phone_login")
        val userInfo: Data = Json.decodeFromJsonElement(json)
        val userData: UserData = Json.decodeFromJsonElement(userInfo.data)
        userId = userData.uid
        return "${userData.nickname}登录成功"
    }

}