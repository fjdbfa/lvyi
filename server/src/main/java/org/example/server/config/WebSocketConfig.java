package org.example.server.config;
import org.example.server.handler.ChatWebSocketHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;

    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

/**
 * 重写父类的 WebSocket 处理器注册方法，用于配置 WebSocket 连接的端点和拦截器
 * @param registry WebSocket 处理器注册对象，用于注册 WebSocket 处理器和相关配置
 */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    // 注册 WebSocket 处理器，并配置连接端点和握手拦截器
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
            // 添加握手拦截器，用于在 WebSocket 握手前进行验证和处理
                .addInterceptors(new HandshakeInterceptor() {
                /**
                 * 在 WebSocket 握手之前执行，用于验证和预处理连接参数
                 * @param request 当前 HTTP 请求对象
                 * @param response 当前 HTTP 响应对象

                 * @param attributes 用于存储 WebSocket 会话属性的 Map
                 * @return 是否允许握手继续进行
                 */
                    @Override
                    public boolean beforeHandshake(@NotNull ServerHttpRequest request,
                                                   @NotNull ServerHttpResponse response,
                                                   @NotNull WebSocketHandler wsHandler,
                                                   @NotNull Map<String, Object> attributes) throws Exception {
                        // 从 URL 参数解析 senderId
                        String query = request.getURI().getQuery();
                        if (query != null && query.contains("senderId=")) {
                            String[] params = query.split("&");
                            for (String param : params) {
                                if (param.startsWith("senderId=")) {
                                    String senderIdStr = param.substring("senderId=".length());
                                    try {
                                        Long senderId = Long.parseLong(senderIdStr);
                                        attributes.put("SENDER_ID", senderId);
                                        return true;
                                    } catch (NumberFormatException e) {
                                        response.setStatusCode(HttpStatus.BAD_REQUEST); // 增加错误反馈
                                        return false;
                                    }
                                }
                            }
                        }
                        return false; // 拒绝无 senderId 的连接
                    }

                /**
                 * 在 WebSocket 握手完成后执行
                 * @param request 当前 HTTP 请求对象
                 * @param response 当前 HTTP 响应对象
                 *
                 * @param exception 握手过程中发生的异常（如果有）
                 */
                    @Override
                    public void afterHandshake(org.springframework.http.server.ServerHttpRequest request,
                                               org.springframework.http.server.ServerHttpResponse response,
                                               org.springframework.web.socket.WebSocketHandler wsHandler,
                                               Exception exception) {
                        // 握手完成后执行的操作（可留空）
                    }
                })
                .setAllowedOrigins("*"); // 生产环境请指定域名
    }
}