package org.laolittle.plugin.caiyun

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("CaiyunConfig") {
    @ValueDescription("用户ID")
    var userId: String by value("")
    @ValueDescription("使用的续写模型")
    var modelId: String by value("60094a2a9661080dc490f75a")
}