package org.example.server.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;

import java.io.IOException;

/**
 * ChatMessageSerializer 类用于将 ChatMessage 对象序列化为 JSON 格式。
 * 它继承自 JsonSerializer<ChatMessage>，是一个自定义的 JSON 序列化器。
 */
public class ChatMessageSerializer extends JsonSerializer<ChatMessage> {
    /**
     * 重写 serialize 方法，将 ChatMessage 对象转换为 JSON 格式。
     *
     * @param value 要序列化的 ChatMessage 对象
     * @param gen 用于生成 JSON 的 JsonGenerator 对象
     * @param serializers 序列化提供者，可用于获取序列化器
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    public void serialize(ChatMessage value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        // 开始写入 JSON 对象
        gen.writeStartObject();
        // 根据具体的消息类型写入不同的字段
        if (value instanceof UserMessage) {
            // 如果是用户消息，设置类型为 "user" 并写入文本内容
            gen.writeStringField("type", "user");
            gen.writeStringField("text", ((UserMessage) value).text());
        } else if (value instanceof AiMessage) {
            // 如果是 AI 消息，设置类型为 "ai" 并写入文本内容
            gen.writeStringField("type", "ai");
            gen.writeStringField("text", ((AiMessage) value).text());
        } else if (value instanceof SystemMessage) {
            // 如果是系统消息，设置类型为 "system" 并写入文本内容
            gen.writeStringField("type", "system");
            gen.writeStringField("text", ((SystemMessage) value).text());
        } else {
            // 处理其他未知类型，设置类型为 "unknown" 并写入对象的字符串表示
            gen.writeStringField("type", "unknown");
            gen.writeStringField("text", value.toString());
        }

        // 结束 JSON 对象的写入
        gen.writeEndObject();
    }
}
