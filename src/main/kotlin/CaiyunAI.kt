package org.laolittle.plugin.caiyun

import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.whileSelectMessages
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.caiyun.Config.userId
import org.laolittle.plugin.caiyun.api.CaiyunApiService.loginFromCode
import org.laolittle.plugin.caiyun.api.CaiyunApiService.sendVerification

@ExperimentalSerializationApi
object CaiyunAI : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.caiyun.CaiyunAI",
        name = "CaiyunAI",
        version = "1.0.1",
    ) {
        author("LaoLittle")
    }
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
        Config.reload()
        if (userId == "") logger.info { "请私聊机器人登录彩云小梦" }
        else GroupMessageListener.start()
        GlobalEventChannel.subscribeFriendMessages {
            "#登录" Login@{
                var codeId = ""
                var phoneNumber: Long = 0
                subject.sendMessage("请在2分钟内输入你的手机号")
                whileSelectMessages {
                    default {
                        if (this.message.content.contains(Regex("""\D"""))) {
                            subject.sendMessage("请输入正确的手机号")
                            return@default true
                        }
                        phoneNumber = this.message.content.toLong()
                        try {
                            codeId = sendVerification(phoneNumber)
                        } catch (e: Exception) {
                            subject.sendMessage(codeId)
                            return@default true
                        }
                        false
                    }
                    timeout(120_000) {
                        subject.sendMessage("超时")
                        false
                    }
                }
                if (codeId == "") return@Login
                subject.sendMessage("请在5分钟内输入你所收到的验证码")
                whileSelectMessages {
                    default Here@{
                        if (this.message.content.contains(Regex("""\D"""))) {
                            subject.sendMessage("请输入正确的验证码")
                            return@Here true
                        }
                        subject.sendMessage(loginFromCode(codeId, this.message.content.toInt(), phoneNumber))
                        false
                    }
                    timeout(300_000) {
                        subject.sendMessage("超时未输入")
                        false
                    }
                }
            }
        }
    }
}