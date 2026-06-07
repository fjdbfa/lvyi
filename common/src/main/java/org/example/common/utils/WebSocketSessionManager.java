package org.example.common.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * WebSocket会话管理器
 * 用于管理和维护用户WebSocket会话的映射关系
 */
@Component
public class WebSocketSessionManager {
    // key: userId, value: session
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void addSession(Long userId, WebSocketSession session) {
        // 防止并发 sendMessage 导致 Tomcat TEXT_PARTIAL_WRITING 异常
        // sendTimeLimitMs: 单次发送允许的最大耗时
        // bufferSizeLimitBytes: 待发送消息缓冲区上限（超出会抛异常，避免内存无限增长）
        WebSocketSession safeSession = new ConcurrentWebSocketSessionDecorator(session, 10_000, 512 * 1024);
        sessions.put(userId, safeSession);
    }

    public void removeSession(Long userId) {
        sessions.remove(userId);
    }

    public WebSocketSession getSession(Long userId) {
        return sessions.get(userId);
    }

    public boolean isOnline(Long userId) {
        WebSocketSession session = sessions.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 获取所有在线用户的ID列表
     * @return 在线用户ID列表
     */
    public List<Long> getOnlineUsers() {
        return sessions.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().isOpen())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 获取在线用户的数量
     * @return 在线用户数量
     */
    public int getOnlineUserCount() {
        return getOnlineUsers().size();
    }
}