package org.laolittle.plugin.caiyun

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("CaiyunConfig") {
    @ValueDescription("UserId")
    var userId: String by value("")
}