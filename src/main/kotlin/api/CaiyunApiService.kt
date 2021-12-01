package org.laolittle.plugin.caiyun.api

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import org.laolittle.plugin.caiyun.Config.userId
import org.laolittle.plugin.caiyun.model.*
import org.laolittle.plugin.caiyun.model.Json
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection

@ExperimentalSerializationApi
object CaiyunApiService {
    fun startWrite(title: String, text: String, nodeId: String, novelId: String): Novel {
        val conn = setConnection("https://if.caiyunai.com/v2/novel/$userId/novel_ai")

        val out = DataOutputStream(conn.outputStream)
        val json = buildJsonObject {
            put("content", text)
            put("lang", "zh")
            put("lastnode", nodeId)
            put("mid", "601f92f60c9aaf5f28a6f908")
            put("nid", novelId)
            put("ostype", "")
            put("status", "http")
            put("storyline", false)
            put("title", title)
            put("uid", userId)
        }
        out.use {
            it.writeChars(json.toString())
            it.flush()
        }

        conn.connect()
        conn.disconnect()

        val jsonStr = getString(conn.inputStream)
        val data = Json.decodeFromString<Data>(jsonStr).data
        val nodes = Json.decodeFromJsonElement<Nodes>(data).nodes
        return Json.decodeFromJsonElement(nodes[0])
    }

    fun getNovelInfo(title: String, text: String, nodeId: Boolean, putNodes: JsonArray = buildJsonArray { }): String {
        val conn = setConnection("https://if.caiyunai.com/v2/novel/$userId/novel_save")

        val out = DataOutputStream(conn.outputStream)
        val json = buildJsonObject {
            put("lang", "zh")
            put("nodes", putNodes)
            put("ostype", "")
            put("text", text)
            put("title", title)
        }
        out.use {
            it.writeChars(json.toString())
            it.flush()
        }

        conn.connect()
        conn.disconnect()

        val jsonStr = getString(conn.inputStream)
        val data = Json.decodeFromString<Data>(jsonStr).data
        val firstNode = Json.decodeFromJsonElement<FirstNode>(data).firstNode
        val n: NodeId = Json.decodeFromJsonElement(firstNode)
        return if (nodeId) n.nodeId else n.nid
        /*
        val firstNode: FirstNode = Json.decodeFromJsonElement(data)
        val nodeId = Json.decodeFromJsonElement<NodeId>(firstNode.node).nodeId
        */

    }

    fun sendVerification(PhoneNumber: Long): String {
        val conn = setConnection("https://if.caiyunai.com/v2/user/phone_message")

        val out = DataOutputStream(conn.outputStream)
        val json = buildJsonObject {
            put("type", "login")
            put("phone", PhoneNumber)
            put("callcode", 86)
            put("uid", "")
            put("lang", "zh")
            put("ostype", "")
        }
        out.use {
            it.writeBytes(json.toString())
            it.flush()
        }

        conn.connect()
        conn.disconnect()

        val jsonStr = getString(conn.inputStream)
        val returnData: Data
        val code: PhoneMessage
        try {
            returnData = Json.decodeFromString(jsonStr)
            code = Json.decodeFromJsonElement(returnData.data)
        } catch (e: Exception) {
            val caiyunStatus: CaiyunStatus = Json.decodeFromString(jsonStr)
            return caiyunStatus.message
        }
        return code.codeId
    }

    fun loginFromCode(codeId: String, code: Int, PhoneNumber: Long): String {
        val conn = setConnection("https://if.caiyunai.com/v2/user/phone_login")

        val out = DataOutputStream(conn.outputStream)
        val json = buildJsonObject {
            put("code", code)
            put("phone", PhoneNumber)
            put("codeid", codeId)
            put("uid", "")
            put("callcode", 86)
            put("lang", "zh")
            put("ostype", "")
        }
        out.use {
            it.writeBytes(json.toString())
            it.flush()
        }

        conn.connect()
        conn.disconnect()

        val jsonStr = getString(conn.inputStream)
        val userInfo: Data
        try {
            userInfo = Json.decodeFromString(jsonStr)
        } catch (e: Exception) {
            return "登录失败，请检查验证码是否正确"
        }
        val userData: UserData = Json.decodeFromJsonElement(userInfo.data)
        userId = userData.uid
        return "${userData.nickname}登录成功"
    }

    private fun setConnection(Url: String): HttpsURLConnection {
        val connection = URL(Url).openConnection() as HttpsURLConnection

        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.useCaches = false
        connection.instanceFollowRedirects = true

        connection.setRequestProperty("Accept", "application/json, text/plain, */*")
        connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
        connection.setRequestProperty("Connection", "keep-alive")
        connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
        connection.setRequestProperty("Host", "if.caiyunai.com")
        connection.setRequestProperty("Origin", "https://if.caiyunai.com")
        connection.setRequestProperty("Referer", "https://if.caiyunai.com/dream/")
        connection.setRequestProperty(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.55 Safari/537.36 Edg/96.0.1054.34"
        )
        return connection
    }

    private fun getString(input: InputStream?): String {
        return if (input != null) {
            val writer: Writer = StringWriter()
            val buffer = CharArray(1024)
            input.use {
                val reader: Reader = BufferedReader(
                    InputStreamReader(input, "UTF-8")
                )
                var n: Int
                while (reader.read(buffer).also { n = it } != -1) {
                    writer.write(buffer, 0, n)
                }
            }
            writer.toString()
        } else {
            ""
        }
    }
}