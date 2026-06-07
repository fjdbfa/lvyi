package org.example.server.handler;


import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import org.example.common.utils.WebSocketSessionManager;
import org.example.pojo.dto.ContactInfoDTO;
import org.example.pojo.entity.UserMessages;
import org.example.pojo.entity.UserMessageSession;
import org.example.pojo.envcontent.UserMessageVo;
import org.example.server.mapper.UserMessageMapper;
import org.example.server.mapper.UserMessageSessionMapper;
import org.example.server.mapper.userDetailedInformationDataMapper;
import org.example.server.sever.UserMessageService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private UserMessageMapper userMessageMapper;

    @Autowired
    private WebSocketSessionManager sessionManager;
    @Autowired
    private  ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从 URL 参数获取 senderId
        Map<String, Object> attributes = session.getAttributes();
        Long senderId = (Long) attributes.get("SENDER_ID");
        if (senderId != null) {
            // 将用户会话添加到管理器中
            sessionManager.addSession(senderId, session);
            // 记录用户上线的日志信息
            log.info("用户 {} 上线", senderId);
            // 广播用户上线消息
            broadcastOnlineStatus(senderId, "online");
            // 发送完整的在线用户列表给新上线的用户
            sendOnlineUsersList(session);
        } else {
            // 当连接未携带 senderId 时记录警告日志
            log.warn("连接未携带 senderId，关闭连接");
            // 关闭无效连接，状态码为 BAD_DATA 表示数据错误
            session.close(CloseStatus.BAD_DATA);
        }
    }

    /**
     * 广播用户在线状态变更
     * @param userId 用户ID
     * @param status 状态：online 或 offline
     */
    private void broadcastOnlineStatus(Long userId, String status) {
        try {
            Map<String, Object> statusMessage = Map.of(
                "userId", userId,
                "status", status
            );
            String message = objectMapper.writeValueAsString(Map.of("info", objectMapper.writeValueAsString(statusMessage)));

            // 向所有在线用户广播状态变更
            for (Long onlineUserId : sessionManager.getOnlineUsers()) {
                WebSocketSession onlineSession = sessionManager.getSession(onlineUserId);
                if (onlineSession != null && onlineSession.isOpen()) {
                    onlineSession.sendMessage(new TextMessage(message));
                }
            }
        } catch (Exception e) {
            log.error("广播在线状态失败", e);
        }
    }

    /**
     * 向指定会话发送完整的在线用户列表
     * @param session WebSocket会话
     */
    private void sendOnlineUsersList(WebSocketSession session) {
        try {
            List<Long> onlineUsers = sessionManager.getOnlineUsers();
            Map<String, Object> statusMessage = Map.of("onlineUsers", onlineUsers);
            String message = objectMapper.writeValueAsString(Map.of("info", objectMapper.writeValueAsString(statusMessage)));

            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        } catch (Exception e) {
            log.error("发送在线用户列表失败", e);
        }
    }


    /**
     * 处理文本消息的方法
     * @param session WebSocket会话对象，表示与客户端的连接
     * @param message 接收到的文本消息
     * @throws Exception 可能抛出的异常
     */
    @Autowired
    private UserMessageService userMessageSessionService;
    @Autowired
    private UserMessageSessionMapper sessionMapper;
    @Autowired
    private userDetailedInformationDataMapper userDetailedInformationDataMapper;

    @Override
    protected void handleTextMessage(@NotNull WebSocketSession session, @NotNull TextMessage message) throws Exception {
        try {
            // 使用ObjectMapper将消息体解析为UserMessage对象
            UserMessageVo userMessages = objectMapper.readValue(message.getPayload(), new TypeReference<UserMessageVo>() {});
            UserMessages messages = BeanUtil.copyProperties(userMessages, UserMessages.class);
            // 从会话属性中获取发送者ID
            Long senderId = (Long) session.getAttributes().get("SENDER_ID");
            // 设置发送者ID、接收者ID和消息创建时间，并将消息存入数据库
            messages.setSenderId(senderId);
            messages.setReceiverId(userMessages.getReceiverId());
            messages.setCreatedAt(LocalDateTime.now());
            userMessageMapper.insert(messages);

            UserMessageSession receiverSession = sessionMapper.selectOne(userMessages.getReceiverId(), senderId);
            if( receiverSession == null) {
                ContactInfoDTO contactInfoDTO2 =  new ContactInfoDTO(userMessages.getReceiverId(), senderId);
                userMessageSessionService. addOrUpdateSession(contactInfoDTO2);
            }
            // 消息投递
            WebSocketSession receiverWebSocket = sessionManager.getSession(userMessages.getReceiverId());
            if (receiverWebSocket != null && receiverWebSocket.isOpen()) {
                String notifyMsg = objectMapper.writeValueAsString(userMessages);
                receiverWebSocket.sendMessage(new TextMessage(notifyMsg));
            } else {
                session.sendMessage(new TextMessage("{\"info\":\"对方不在线，已存离线消息\"}"));
            }
        } catch (JsonProcessingException e) {
            log.warn("消息解析错误: {}", message.getPayload());
            log.info(e.getMessage());
            session.sendMessage(new TextMessage("{\"error\":\"消息格式错误\"}"));
        } catch (Exception e) {
            log.error("处理消息失败", e);
            session.sendMessage(new TextMessage("{\"error\":\"系统错误\"}"));
        }
    }



    /**
 * 当WebSocket连接关闭后调用的方法
 * @param session WebSocket会话对象
 * @param status 连接关闭状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    // 从会话属性中获取发送者ID
        Long senderId = (Long) session.getAttributes().get("SENDER_ID");
    // 检查senderId是否为空
        if (senderId != null) {
        // 如果不为空，则从会话管理器中移除该会话
            sessionManager.removeSession(senderId);
        // 记录用户下线日志
            log.info("用户 {} 下线", senderId);
        // 广播用户下线消息
            broadcastOnlineStatus(senderId, "offline");
        }
    }

/**
 * 处理WebSocket传输错误的方法
 * 当WebSocket连接在数据传输过程中发生错误时，此方法会被调用
 *
 * @param session WebSocket会话对象，表示当前的WebSocket连接
 * @param exception 发生的异常对象，包含错误信息
 * @throws Exception 可能抛出的异常
 */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // 获取发送者ID
        Long senderId = (Long) session.getAttributes().get("SENDER_ID");

        // 处理EOFException（客户端异常断开）
        if (exception instanceof java.io.EOFException) {
            log.warn("WebSocket连接被客户端异常断开，用户ID: {}", senderId);
            // 从会话管理器中移除该会话
            if (senderId != null) {
                sessionManager.removeSession(senderId);
                // 广播用户下线消息
                broadcastOnlineStatus(senderId, "offline");
            }
            return;
        }

        // 其他错误情况
        log.error("WebSocket 传输错误，用户ID: {}", senderId, exception);
        // 关闭WebSocket连接，并返回服务器错误状态码
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }
}