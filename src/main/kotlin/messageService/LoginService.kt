package org.laolittle.plugin.caiyun.messageService

import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.whileSelectMessages
import net.mamoe.mirai.message.data.content
import org.laolittle.plugin.caiyun.Service
import org.laolittle.plugin.caiyun.api.CaiyunApiService
import org.laolittle.plugin.caiyun.utils.onEnable

@ExperimentalSerializationApi
object LoginService : Service() {
    override suspend fun main() {
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
                            codeId = CaiyunApiService.sendVerification(phoneNumber)
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
                        subject.sendMessage(
                            CaiyunApiService.loginFromCode(
                                codeId,
                                this.message.content.toInt(),
                                phoneNumber
                            )
                        )
                        if (!onEnable)
                            FollowUpListener.start()
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