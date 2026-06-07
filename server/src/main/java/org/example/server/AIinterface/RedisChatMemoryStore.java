package org.example.server.AIinterface;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;

import static org.example.common.constant.RedisConstants.CHAT_MEMORY_PREFIX;
import static org.example.common.constant.RedisConstants.CHAT_MEMORY_TTL;

@Slf4j
@Component

public class RedisChatMemoryStore implements ChatMemoryStore {

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private  final ObjectMapper objectMapper ;

    public RedisChatMemoryStore(RedisTemplate<String, Object> redisTemplate,
                                StringRedisTemplate stringRedisTemplate,
                                @Qualifier("chatMemoryObjectMapper")ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        try {

            @SuppressWarnings("unchecked")
                    String json =    stringRedisTemplate.opsForValue().get(getKey(memoryId));
            if (json == null || json.isEmpty()) {
                return new ArrayList<>();
            }
            // ✅ 关键：明确指定 List<ChatMessage> 类型
            return objectMapper.readValue(json, new TypeReference<List<ChatMessage>>() {});

        } catch (Exception e) {
            log.error("从 Redis 获取用户 {} 的聊天记录失败", memoryId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        try {

            String json = objectMapper.writeValueAsString(messages);
            stringRedisTemplate.opsForValue().set(getKey(memoryId), json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save chat messages", e);
        }
    }

    @NotNull
    private static String getKey(Object memoryId) {
        String key = CHAT_MEMORY_PREFIX + memoryId;
        return key;
    }

    @Override
    public void deleteMessages(Object memoryId) {
        try {

            redisTemplate.delete(getKey(memoryId));
        } catch (Exception e) {
            log.error("删除用户 {} 的聊天记录失败", memoryId, e);
        }
    }
}