package org.laolittle.plugin.caiyun.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.whileSelectMessages

class ConversationBuilder(
    val conversationBlock: suspend ConversationBuilder.() -> Unit
) {
    suspend operator fun invoke() = conversationBlock()
}

suspend fun conversation(
    scope: CoroutineScope,
    block: suspend ConversationBuilder.() -> Unit
): ConversationBuilder {
    suspend fun execute() = ConversationBuilder(
        conversationBlock = block
    ).also { it() }

    return withContext(scope.coroutineContext + scope.coroutineContext) { execute() }
}

suspend fun MessageEvent.loopWrite(title: String,textIn: String ,ctx: CoroutineScope): Boolean {
    var text = textIn
    var textKeep: String
    var keepLoop = true
    conversation(ctx){
        whileSelectMessages {
            "继续" {
                false
            }
            timeout(30_000){
                subject.sendMessage("不继续了吗？那我走了")
                keepLoop = false
                false
            }
        }
    }
    return keepLoop
}