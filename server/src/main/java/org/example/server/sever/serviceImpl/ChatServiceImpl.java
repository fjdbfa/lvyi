package org.example.server.sever.serviceImpl;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.common.utils.UserHolder;
import org.example.pojo.envcontent.ChatMessageListVo;
import org.example.server.AIinterface.Assistant;
import org.example.server.AIinterface.RedisChatMemoryStore;
import org.example.server.sever.ChatService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {
    // AI助手组件，用于处理与AI的对话逻辑


    private final Assistant assistant; // LangChain4j 会自动代理实现
    private final RedisChatMemoryStore redisChatMemoryStore;
    /**
     * 与 AI 对话（带用户 + 聊天框维度的记忆隔离）
     *
     * @param      （如 "chat_1001"）
     * @param
     * @return AI 回复
     */
    public String chat(String message, Long userId) {

//        Long userId = Long.valueOf(UserHolder.getUser().getId()); // 假设返回 Long，你根据实际情况调整

        // 2. 组合唯一 memoryId：格式建议 "user:{userId}:box:{boxId}"
        String memoryId = getMemoryId("chat:", userId);

        // 3. 调用 AiService，LangChain4j 会自动：
        //    - 用 memoryId 从 Redis 加载历史
        //    - 调用模型
        //    - 保存新对话到 Redis
        return assistant.chat(memoryId, message);
    }



    /**
     * 清除指定用户在指定聊天框中的内存数据
     * @param boxId 聊天框的唯一标识符
     * @param message 要清除的消息内容（此参数在当前实现中未使用）
     * @throws IllegalStateException 当用户未登录时抛出此异常
     */
    public void clearMemory(String boxId) {
        Long userId = Long.valueOf(UserHolder.getUser().getId()); // 假设返回 Long，你根据实际情况调整

        // 2. 组合唯一 memoryId：格式建议 "user:{userId}:box:{boxId}"
        String memoryId = getMemoryId(boxId, userId);
        redisChatMemoryStore.deleteMessages(memoryId);
    }

    @Override
    public Result<List<ChatMessageListVo>> getHistory(Long userId) {
        try {

            log.info("获取聊天记录，userId: {}", userId);
            List<ChatMessage> messages = redisChatMemoryStore.getMessages(getMemoryId("chat:", userId));
            List<ChatMessageListVo> dtos = messages.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return  Result.success(dtos);
        } catch (Exception e) {
            return  Result.error("获取聊天记录出错: " + e.getMessage());
        }
    }
    private ChatMessageListVo convertToDto(ChatMessage message) {
        if (message instanceof UserMessage) {
            return new ChatMessageListVo("user", ((UserMessage) message).text());
        } else if (message instanceof AiMessage) {
            return new ChatMessageListVo("ai", ((AiMessage) message).text());
        } else if (message instanceof SystemMessage) {
            return new ChatMessageListVo("system", ((SystemMessage) message).text());
        }
        return new ChatMessageListVo("unknown", message.toString());
    }
    @NotNull
    private static String getMemoryId(String boxId, Long userId) {
        return "user:" + userId + ":box:" + boxId;
    }

    /**
     * 清除指定用户聊条框的聊天记录
     * @param boxId 聊天框ID
     */

    // ✅ 新增：删除某个聊天框的记忆

}
