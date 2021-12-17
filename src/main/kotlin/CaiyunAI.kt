package org.laolittle.plugin.caiyun

import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info
import org.laolittle.plugin.caiyun.Config.userId
import org.laolittle.plugin.caiyun.messageService.FollowUpListener
import org.laolittle.plugin.caiyun.messageService.LoginService
import org.laolittle.plugin.caiyun.messageService.ModelSwitchListener
import org.laolittle.plugin.caiyun.utils.onEnable

@ExperimentalSerializationApi
object CaiyunAI : KotlinPlugin(
    JvmPluginDescription(
        id = "org.laolittle.plugin.caiyun.CaiyunAI",
        name = "CaiyunAI",
        version = "1.0.2",
    ) {
        author("LaoLittle")
    }
) {
    override fun onEnable() {
        logger.info { "加载完毕" }
        Config.reload()
        if (userId == "") logger.info { "请私聊机器人登录彩云小梦" }
        else {
            FollowUpListener.start()
            ModelSwitchListener.start()
            onEnable = true
        }
        LoginService.start()
    }

    override fun onDisable() {
        logger.info { "卸载完毕" }
    }
}