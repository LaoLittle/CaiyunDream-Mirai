package org.laolittle.plugin.caiyun.messageService

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.event.whileSelectMessages
import net.mamoe.mirai.utils.error
import org.laolittle.plugin.caiyun.CaiyunAI
import org.laolittle.plugin.caiyun.Config.modelId
import org.laolittle.plugin.caiyun.Service
import org.laolittle.plugin.caiyun.api.CaiyunApiService.getModels
import org.laolittle.plugin.caiyun.model.Json
import org.laolittle.plugin.caiyun.model.ModelInfo

@ExperimentalSerializationApi
object ModelSwitchListener : Service() {
    override suspend fun main() {
        GlobalEventChannel.subscribeMessages {
            "#更换模型" Here@{
                var modelList: Map<Int, String> = linkedMapOf()
                val modelListJson: JsonArray
                var modelNames = ""
                try {
                    modelListJson = getModels()
                } catch (e: Exception) {
                    CaiyunAI.logger.error { "获取失败: $e" }
                    return@Here
                }
                for ((i, json) in modelListJson.withIndex()) {
                    val model: ModelInfo = Json.decodeFromJsonElement(json)
                    modelList = modelList.plus(i+1 to model.modelId)
                    modelNames += "${i + 1}. ${model.modelName}\n"
                }
                subject.sendMessage("找到如下结果\n" + modelNames + "请输入需要更换的模型的序号")
                whileSelectMessages {
                    default Receive@{ msg ->
                        if (Regex("""\D""").containsMatchIn(msg))
                            subject.sendMessage("请输入数字 !")
                        else if ((msg.toInt() > modelList.size) or (msg.toInt() <= 0))
                            subject.sendMessage("请输入正确的数字 !")
                        else {
                            modelId = modelList[msg.toInt()]!!
                            subject.sendMessage("成功更换模型")
                            return@Receive false
                        }
                        true
                    }
                }
            }
        }
    }
}