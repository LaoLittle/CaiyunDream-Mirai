package org.laolittle.plugin.caiyun.utils

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonElement
import org.laolittle.plugin.caiyun.model.Json

object KtorHttpUtil {
    private val client = HttpClient()
    private val ua = setOf(
        "Mozilla/5.0 (Windows; U; MSIE 9.0; Windows NT 9.0; en-US);",
        "Mozilla/5.0 (compatible; MSIE 10.0; Macintosh; Intel Mac OS X 10_7_3; Trident/6.0)'",
        "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/4.0; GTB7.4; InfoPath.2; SV1; .NET CLR 3.3.69573; WOW64; en-US)",
        "Opera/9.80 (X11; Linux i686; U; ru) Presto/2.8.131 Version/11.11",
        "Mozilla/5.0 (Windows NT 6.2; Win64; x64; rv:16.0.1) Gecko/20121011 Firefox/16.0.1",
        "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:15.0) Gecko/20100101 Firefox/15.0.1",
        "Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25"
    )

    suspend fun get(url: String): JsonElement {
        return Json.parseToJsonElement(client.get{
            url(url)
            header("Content-Type", "application/json;charset=utf-8")
            header("User-Agent", ua.random())
        })

    }

    suspend fun String.post(url: String): JsonElement {
        val responseData = client.post<String> {
            url(url)
            body = this@post
            header("Content-Type", "application/json;charset=utf-8")
            header("User-Agent", ua.random())
        }
        return Json.parseToJsonElement(responseData)
    }
}