package org.laolittle.plugin.caiyun

import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.whileSelectMessages
import net.mamoe.mirai.message.data.content
import org.laolittle.plugin.caiyun.api.CaiyunApiService
import org.laolittle.plugin.caiyun.api.CaiyunApiService.startWrite
import org.laolittle.plugin.caiyun.model.Novel
import org.laolittle.plugin.caiyun.utils.inActMember

@ExperimentalSerializationApi
object GroupMessageListener : Service() {
    override suspend fun main() {
        GlobalEventChannel.subscribeGroupMessages {
            "续写" Start@{
                if (inActMember.contains(sender.id)) return@Start
                inActMember.add(sender.id)
                var nodeId = ""
                var novelMsg = ""

                subject.sendMessage("请输入标题")
                var title = ""
                whileSelectMessages {
                    default {
                        title = this.message.content
                        subject.sendMessage("请输入你想要续写的正文")
                        false
                    }
                    timeout(20_000) {
                        subject.sendMessage("超时")
                        false
                    }
                }
                whileSelectMessages {
                    default {
                        val msg = this.message.content
                        val novel: Novel
                        nodeId = CaiyunApiService.getNovelInfo(title, msg, true)
                        val nid = CaiyunApiService.getNovelInfo(title, msg, false)
                        try {
                            novel = startWrite(title, msg, nodeId, nid)
                        } catch (e: Exception) {
                            subject.sendMessage("被玩坏了...这绝对不是我的错！绝对！")
                            return@default false
                        }
                        novelMsg = msg + novel.content
                        subject.sendMessage(novelMsg)
                        nodeId = novel.nodeId
                        false
                    }
                    timeout(120_000) {
                        subject.sendMessage("超时")
                        false
                    }
                }
                var keepLoop = true
                while (keepLoop && nodeId != "") {
                    whileSelectMessages {
                        "继续" Here@{
                            val msg = novelMsg
                            val novel: Novel
                            val nid = CaiyunApiService.getNovelInfo(title, msg, false)
                            try {
                                novel = startWrite(title, msg, nodeId, nid)
                            } catch (e: Exception) {
                                subject.sendMessage("被玩坏了...这绝对不是我的错！绝对！")
                                return@Here false
                            }
                            novelMsg += novel.content
                            subject.sendMessage(novelMsg)
                            nodeId = novel.nodeId
                            false
                        }
                        startsWith("停") {
                            subject.sendMessage("好")
                            keepLoop = false
                            false
                        }
                        timeout(30_000) {
                            subject.sendMessage("不继续了？那我走了")
                            keepLoop = false
                            false
                        }
                    }
                }
                inActMember.remove(sender.id)
            }
        }
    }
}

