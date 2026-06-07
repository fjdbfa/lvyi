package org.example.common.constant;

import java.time.Duration;

/**
 * RedisConstants类用于存储Redis相关的常量定义
 * 包含用户登录和聊天记忆相关的键前缀和过期时间设置
 */
public class RedisConstants {
    /**
     * 用户登录令牌的键前缀
     * 用于在Redis中存储用户登录信息的键名前缀
     */
    public static  final String USER_LOGIN_KEY = "login:token:";
    /**
     * 用户登录令牌的过期时间（秒）
     * 设置为3600秒，即1小时后过期
     */
    public static  final  Long USER_LOGIN_TTL = 3600L;
    /**
     * 聊天记忆的键前缀
     * 用于在Redis中存储聊天记忆信息的键名前缀
     */
    public static final String CHAT_MEMORY_PREFIX = "chat_memory:";
    /**
     * 聊天记忆的过期时间
     * 设置为24小时后过期
     */
    public static final Duration CHAT_MEMORY_TTL = Duration.ofHours(24);

    public static final String REDIS_TASK_KEY_PREFIX = "tasks:user:";
    public static final long CACHE_TTL_SECONDS = 3600; // 单位：秒
     public static final String CONVERSATION_CACHE_KEY_PREFIX = "UserMessages:conversations:";
    public static final String CHAT_UNREAD = "chat:unread:";
    public static final String LOGIN_TOKEN = "login:token:";
}
