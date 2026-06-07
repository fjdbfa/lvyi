package org.example.server.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;

import java.io.IOException;

/**
 * ChatMessageDeserializer 类用于将 JSON 数据反序列化为 ChatMessage 对象。
 * 它支持多种 JSON 格式，能够根据不同的字段结构识别消息类型。
 */
public  class ChatMessageDeserializer extends JsonDeserializer<ChatMessage> {
    /**
     * 重写 deserialize 方法，将 JSON 数据反序列化为 ChatMessage 对象。
     * 该方法会尝试从多种可能的 JSON 结构中识别消息类型。
     *
     * @param p JsonParser 对象，用于解析 JSON 数据
     * @param DeserializationContext 对象，提供反序列化上下文
     * @return 反序列化后的 ChatMessage 对象
     * @throws IOException 如果发生 I/O 错误
     */
    @Override
    public ChatMessage deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // 尝试从多种可能的结构中识别消息类型

        // 方法1: 检查是否有 type 字段
        if (node.has("type")) {
            String type = node.get("type").asText().toLowerCase();
            return deserializeByTypeField(node, type);
        }

        // 方法2: 检查是否有 name 字段且 contents 字段（LangChain4j 的序列化格式）
        if (node.has("name") && node.has("contents")) {
            return deserializeByContentsField(node);
        }

        // 方法3: 检查是否有 text 字段
        if (node.has("text")) {
            String text = node.get("text").asText();
            // 默认当作 UserMessages 处理，可根据需要调整
            return UserMessage.from(text);
        }

        // 方法4: 检查是否有 content 字段
        if (node.has("content")) {
            String content = node.get("content").asText();
            // 根据上下文判断，如果是 AI 消息
            return AiMessage.from(content);
        }

        throw new IllegalArgumentException("Cannot determine message type from JSON: " + node.toString());
    }

    private ChatMessage deserializeByTypeField(JsonNode node, String type) {
        switch (type) {
            case "user":
                String userText = extractTextFromNode(node);
                return UserMessage.from(userText);

            case "ai":
            case "assistant":
                String aiText = extractTextFromNode(node);
                return AiMessage.from(aiText);

            case "system":
                String systemText = extractTextFromNode(node);
                return SystemMessage.from(systemText);

            default:
                throw new IllegalArgumentException("Unknown message type: " + type);
        }
    }

/**
 * 从JsonNode中反序列化ChatMessage对象，主要处理contents字段
 * 该方法专门针对LangChain4j的序列化格式: {"name":null,"contents":[{"text":"你好"}]}
 *
 * @param node 包含聊天消息内容的JsonNode对象
 * @return 反序列化后的ChatMessage对象，通常是UserMessage类型
 * @throws IllegalArgumentException 当无法从contents中提取文本时抛出
 */
    private ChatMessage deserializeByContentsField(JsonNode node) {
        // LangChain4j 序列化格式: {"name":null,"contents":[{"text":"你好"}]}
        JsonNode contentsNode = node.get("contents");
        if (contentsNode != null && contentsNode.isArray() && contentsNode.size() > 0) {
            JsonNode firstContent = contentsNode.get(0);
            if (firstContent.has("text")) {
                String text = firstContent.get("text").asText();

                // 根据 name 字段或其他线索判断类型
                // 如果 name 为 null 且没有其他线索，可以根据内容判断
                // 通常这种格式是 UserMessages
                return UserMessage.from(text);
            }
        }

        // 如果无法从 contents 提取文本，抛出异常
        throw new IllegalArgumentException("Cannot extract text from contents: " + node.toString());
    }

/**
 * 从JsonNode节点中提取文本内容
 * 该方法会尝试从多个可能的字段中获取文本，包括"text"、"content"以及"contents"数组中的第一个元素的"text"字段
 *
 * @param node 要提取文本的JsonNode节点
 * @return 提取到的文本内容，如果找不到任何文本字段则返回空字符串
 */
    private String extractTextFromNode(JsonNode node) {
        // 尝试多种可能的文本字段
        if (node.has("text")) {
            return node.get("text").asText();
        } else if (node.has("content")) {
            return node.get("content").asText();
        } else if (node.has("contents") && node.get("contents").isArray()) {
            JsonNode contentsNode = node.get("contents");
            if (contentsNode.size() > 0) {
                JsonNode firstContent = contentsNode.get(0);
                if (firstContent.has("text")) {
                    return firstContent.get("text").asText();
                }
            }
        }
        return "";
    }

}
