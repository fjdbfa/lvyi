package org.example.server.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import org.example.server.AIinterface.RedisChatMemoryStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Configuration
public class LangChain4jConfig {

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;

    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Value("${doubao.chat.base-url}")
    private String doubaoBaseUrl;

    @Value("${doubao.chat.api-key}")
    private String doubaoApiKey;

    @Value("${doubao.chat.model-name}")
    private String doubaoModelName;

    @Bean
    @Primary
    public ChatLanguageModel opAiChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .logRequests(true)
                .logResponses(true)

                .build();
    }

    @Bean
    public ChatLanguageModel doubaoChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(doubaoBaseUrl)
                .apiKey(doubaoApiKey)
                .modelName(doubaoModelName)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    /**
     * 创建并配置一个流式聊天语言模型的Bean
     * 该方法使用OpenAI的聊天模型，并配置了必要的参数
     *
     * @return 配置好的StreamingChatLanguageModel实例
     */
    @Bean
    public StreamingChatLanguageModel opAiStreamingChatModel() {
        // 使用建造者模式创建OpenAiStreamingChatModel实例
        return OpenAiStreamingChatModel.builder()
                // 设置API的基础URL
                .baseUrl(baseUrl)
                // 设置API密钥
                .apiKey(apiKey)
                // 指定要使用的模型名称
                .modelName(modelName)
                // 启用请求日志记录
                .logRequests(true)
                // 启用响应日志记录
                .logResponses(true)
                // 构建并返回配置完成的实例
                .build();
    }

    @Bean
    public StreamingChatLanguageModel doubaoStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(doubaoBaseUrl)
                .apiKey(doubaoApiKey)
                .modelName(doubaoModelName)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    /**
     * 创建并配置一个ChatMemoryStore Bean，使用Redis作为存储后端
     *
     * @param redisTemplate Redis模板，用于操作Redis数据库
     * @return 返回一个基于Redis的聊天内存存储实现
     */

    @Bean
    public ChatMemoryStore chatMemoryStore(RedisTemplate<String, Object> redisTemplate,
                                           StringRedisTemplate stringRedisTemplate,
                                           @Qualifier("chatMemoryObjectMapper")  ObjectMapper objectMapper) {
        // 使用提供的RedisTemplate创建RedisChatMemoryStore实例
        return new RedisChatMemoryStore(redisTemplate, stringRedisTemplate,  objectMapper);
    }
    /**
     * 创建并配置一个ChatMemory Bean，用于管理聊天消息的内存存储
     *
     * @param chatMemoryStore 聊天消息的存储接口实现，用于持久化消息
     * @return 配置好的MessageWindowChatMemory实例，最多保存20条消息
     */
    @Bean
    public ChatMemoryProvider chatMemoryProvider(ChatMemoryStore chatMemoryStore) {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .chatMemoryStore(chatMemoryStore)
                .build();
    }
    @Bean("environmentalAssistantChatMemoryProvider")
    public ChatMemoryProvider environmentalAssistantChatMemoryProvider(ChatMemoryStore chatMemoryStore) throws IOException, IOException {
        // 从文件读取系统消息
        ClassPathResource resource = new ClassPathResource("system-messages/environmental-assistant.txt");
        String systemMessageContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);

        return memoryId -> {
            MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                    .id(memoryId)
                    .maxMessages(20)
                    .chatMemoryStore(chatMemoryStore)
                    .build();

            // 添加系统消息到聊天记忆
            SystemMessage systemMessage = SystemMessage.from(systemMessageContent);
            chatMemory.add(systemMessage);

            return chatMemory;
        };
    }

}