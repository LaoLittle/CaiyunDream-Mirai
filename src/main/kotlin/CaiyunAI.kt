package org.laolittle.plugin.caiyun

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.whileSelectMessages
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.caiyun.api.CaiyunApiService.loginFromCode
import org.laolittle.plugin.caiyun.api.CaiyunApiService.sendVerification
import org.laolittle.plugin.caiyun.api.CaiyunApiService.getNovelId
import org.laolittle.plugin.caiyun.api.CaiyunApiService.startWrite

object CaiyunAI : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.caiyun.CaiyunAI",
        name = "CaiyunAI",
        version = "1.0",
    ) {
        author("LaoLittle")
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        Config.reload()
        GlobalEventChannel.subscribeFriendMessages {
            "#登录" {
                subject.sendMessage("请输入你的手机号")
                whileSelectMessages {
                    default {
                        if (this.message.content.contains(Regex("""\D"""))){
                            subject.sendMessage("请输入正确的手机号")
                            return@default true
                        }
                            val phoneNumber = this.message.content.toLong()
                            val codeId = sendVerification(phoneNumber)
                            subject.sendMessage("请在两分钟内输入你所收到的验证码")
                            whileSelectMessages {
                                default Here@{
                                    if (this.message.content.contains(Regex("""\D"""))) {
                                        subject.sendMessage("请输入正确的验证码")
                                        return@Here true
                                    }
                                    subject.sendMessage(loginFromCode(codeId, this.message.content.toInt(), phoneNumber))
                                    Config.reload()
                                    false
                                }
                                timeout(120_000){
                                    subject.sendMessage("超时未输入")
                                    false
                                }
                            }
                        false
                    }
                    timeout(60_000) {
                        subject.sendMessage("超时")
                        false
                    }
                }
            }

        }
        GlobalEventChannel.subscribeGroupMessages {
            "te" {
                subject.sendMessage(getNovelId("1233", "1244", true))
            }
            "续写" {
                subject.sendMessage("请输入标题")
                whileSelectMessages {
                    default {
                        val title = this.message.content
                        subject.sendMessage("请输入你想要续写的正文")
                        whileSelectMessages {
                            default {
                                val msg = this.message.content
                                var nodeId = getNovelId(title, msg, true)
                                val nid = getNovelId(title, msg, false)
                                subject.sendMessage(msg + startWrite(title, msg, nodeId, nid))
                                false
                            }
                            timeout(120_000){
                                subject.sendMessage("超时")
                                false
                            }
                        }
                        false
                    }
                    timeout(10_000){
                        subject.sendMessage("超时未输入")
                        false
                    }
                }
            }
        }
    }
}