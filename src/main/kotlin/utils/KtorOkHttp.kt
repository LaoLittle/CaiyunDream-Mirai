package org.laolittle.plugin.caiyun.utils

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.serialization.json.JsonElement
import okhttp3.OkHttpClient
import org.laolittle.plugin.caiyun.model.Json
import java.util.concurrent.TimeUnit

object KtorOkHttp {
    private val client : HttpClient
    private const val ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36 Edg/96.0.1054.57"

    suspend fun get(url: String): JsonElement {
        return Json.parseToJsonElement(client.get{
            url(url)
            header("Accept", "application/json")
            header("User-Agent", ua)
        })

    }

    suspend fun String.post(url: String): JsonElement {
        val responseData = client.post<String> {
            url(url)
            body = this@post
            header("Content-Type", "application/json;charset=utf-8")
            header("User-Agent", ua)
        }
        return Json.parseToJsonElement(responseData)
    }

    init {
        OkHttpClient.Builder()
        val okHttpEngine = OkHttp.config {
            config {
                readTimeout(30_000, TimeUnit.MILLISECONDS)
            }
        }
        client = HttpClient(okHttpEngine)
    }
}