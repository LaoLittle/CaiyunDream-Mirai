package org.laolittle.plugin.caiyun.utils

/*
object Okhttp {

    private var client: OkHttpClient = OkHttpClient().newBuilder().connectTimeout(Duration.ofMillis(30_000)).build()
    private const val ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Safari/537.36 Edg/96.0.1054.57"

    fun getModel(url: String): JsonElement {
        val request = Request.Builder().url(url)
            .header("Content-Type", "application/json;charset=utf-8")
            .header("User-Agent", ua)
            .get().build()
        val responseBody = client.newCall(request).execute().body
        return responseBody?.use { Json.parseToJsonElement(it.string()) } ?: throw Exception("响应为空")
    }

    fun String.post(url: String): JsonElement {
        val media = "application/json;charset=utf-8"
        val request = Request.Builder().url(url)
            .header("Content-Type", media)
            .header("User-Agent", ua)
            .post(this.toRequestBody(media.toMediaTypeOrNull())).build()
        val responseBody = client.newCall(request).execute().body
        return responseBody?.use { Json.parseToJsonElement(it.string()) } ?: throw Exception("响应为空")
    }
}*/