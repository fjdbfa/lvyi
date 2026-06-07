package org.example.server.controller;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.service.TokenStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.exception.BusinessException;
import org.example.common.result.Result;

import org.example.common.utils.UserHolder;
import org.example.pojo.dto.ChatAiMessageDTO;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.envcontent.ChatMessageListVo;
import org.example.server.AIinterface.Assistant;
import org.example.server.sever.ChatService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.example.common.constant.RedisConstants.LOGIN_TOKEN;

@Slf4j
@RestController
@RequestMapping("/ai/chat")
@RequiredArgsConstructor
public class ChatController {
    private StringRedisTemplate stringRedisTemplate;
    private final ChatService chatService;
    private final Assistant assistant;

    /**
     * 发送消息并与AI进行对话
     * @param chatAiMessageDto 包含用户消息和用户ID的对象
     * @return AI回复的内容
     */
    @PostMapping("/message")
    public Result<String> chat(@RequestBody ChatAiMessageDTO chatAiMessageDto) {
        try {
            // 使用聊条框和用户ID作为记忆ID，确保不同用户有不同的聊天记录
            String response = chatService.chat( chatAiMessageDto.getMessage(), chatAiMessageDto.getUserId());
            return Result.success(response);
        } catch (Exception e) {
            log.error("AI聊天出错: ", e);
            return Result.error("AI聊天出错: " + e.getMessage());
        }
    }



    /**
     * 清除指定用户的聊天记录
     * @param
     * @return 操作结果
     */
    @DeleteMapping("/memory")
    public Result<String> clearMemory(@RequestParam("userId") String userId) {
        try {
            // 清除指定用户的聊天记忆
            chatService.clearMemory(userId);
            return Result.success("聊天记录已清除");
        } catch (Exception e) {
            log.error("清除聊天记录出错: ", e);
            return Result.error("清除聊天记录出错: " + e.getMessage());
        }
    }

    @GetMapping("/history")
    public Result<List<ChatMessageListVo>> getHistory(@RequestParam Long userId) {
        log.info("获取聊天记录");
        return chatService.getHistory(userId);
    }

    @GetMapping(value = "/stream/{userId}/{message}/{toke}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@PathVariable String userId,@PathVariable String message,@PathVariable String toke) throws JsonProcessingException {

        log.info("开始流式对话");
        SseEmitter emitter = new SseEmitter(60_000L);
        TokenStream tokenStream = assistant.stream(String.valueOf(userId), message);
        
        tokenStream.onNext(token -> {
                    try {
                        emitter.send(token);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                })
                .onComplete(response -> {
                            log.info("✅ 流完成");
                            emitter.complete();
                        }
                )
                .onError(emitter::completeWithError)
                .start();
        
        return emitter;
    }

}