package org.example.server.controller;

import lombok.RequiredArgsConstructor;
import org.example.common.result.Result;
import org.example.pojo.dto.ContactInfoDTO;
import org.example.pojo.envcontent.UserMessageHistoryVo;
import org.example.pojo.envcontent.UserMessageListVo;
import org.example.server.sever.UserMessageService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户消息控制器
 * 提供用户消息相关的RESTful API接口
 */
@RestController
@RequestMapping("/userMessage")
@RequiredArgsConstructor
public class UserMessageController {

    /**
     * 用户消息服务接口
     * 用于处理用户消息相关的业务逻辑
     */
    private final UserMessageService userMessageService;

    /**
     * 获取用户对话列表接口
     * @param userId 用户ID
     * @return 返回用户对话列表
     */
    @GetMapping("/conversations")
    public Result<List<UserMessageListVo>> getConversations(@RequestParam Long userId) {
        return Result.success(userMessageService.getConversationList(userId));
    }
/**
 * 添加会话列表的接口方法
 * @PostMapping 映射HTTP POST请求，处理"/addConversationlist"路径的请求
 *
 * @param contactInfo 接收请求体中的JSON数据，自动转换为ContactInfoDTO对象
 * ContactInfoDTO可能包含会话相关的联系人信息
 *
 * @return 返回一个Result对象，可能包含操作结果的状态和信息
 *
 * 该方法调用userMessageService的addConversationList方法处理具体业务逻辑
 */
    @PostMapping("/addConversationlist")
    public Result addConversationlist(@RequestBody ContactInfoDTO contactInfo) {
        return userMessageService.addOrUpdateSession(contactInfo);
    }
    /**
     * 获取用户聊天历史记录接口
     * @param currentUserId 当前用户ID
     * @param otherUserId 对方用户ID
     * @return 返回两个用户之间的聊天历史记录
     */
    @GetMapping("/history")
    public Result<List<UserMessageHistoryVo>> getHistory(
            @RequestParam Long currentUserId,
            @RequestParam Long otherUserId) {
        return Result.success(userMessageService.getChatHistory(currentUserId, otherUserId));
    }

    /**
     * 发送消息接口
     * @param message 要发送的消息对象
     * @return 返回操作结果
     */

}