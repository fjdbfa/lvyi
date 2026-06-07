package org.example.server.AIinterface;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "opAiChatModel",
        streamingChatModel = "opAiStreamingChatModel",
        chatMemoryProvider = "environmentalAssistantChatMemoryProvider"
)
public interface Assistant {

        /**
         * 聊天方法
         * @param 聊条框，用于标识不同的会话
         * @param message 用户发送的消息内容
         * @return 返回AI助手的回复内容
         */
        String chat(@MemoryId String boxId, @UserMessage String message);

        TokenStream stream(@MemoryId String boxId, @UserMessage String message);
}