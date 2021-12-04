package org.laolittle.plugin.caiyun.utils

import net.mamoe.mirai.message.data.ForwardMessage
import net.mamoe.mirai.message.data.RawForwardMessage


object CustomForwardMsgDisplay : ForwardMessage.DisplayStrategy {
    override fun generateTitle(forward: RawForwardMessage): String = "群聊的聊天记录"

    override fun generateBrief(forward: RawForwardMessage): String = "[续写]"

    override fun generateSummary(forward: RawForwardMessage): String = "续写结果"

    override fun generatePreview(forward: RawForwardMessage): List<String> = listOf("\n")
}