package org.example.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis 静态工具类（解决 Map 泛型擦除问题）
 */
@Component
public class RedisUtils implements ApplicationContextAware {

    private static StringRedisTemplate stringRedisTemplate;
    private static ObjectMapper objectMapper;

    // Spring 启动时自动注入
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        stringRedisTemplate = applicationContext.getBean(StringRedisTemplate.class);
        objectMapper = applicationContext.getBean(ObjectMapper.class);
    }

    // ==================== 普通对象存取 ====================

    public static <T> void set(String key, T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json);
        } catch (Exception e) {
            throw new RuntimeException("Redis set error: " + e.getMessage(), e);
        }
    }

    public static <T> void set(String key, T value, long timeout, TimeUnit unit) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, timeout, unit);
        } catch (Exception e) {
            throw new RuntimeException("Redis set with timeout error: " + e.getMessage(), e);
        }
    }

    public static <T> T get(String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Redis get error: " + e.getMessage(), e);
        }
    }

    // ==================== Map 类型存取（解决泛型擦除） ====================

    public static <K, V> void setMap(String key, Map<K, V> map) {
        try {
            String json = objectMapper.writeValueAsString(map);
            stringRedisTemplate.opsForValue().set(key, json);
        } catch (Exception e) {
            throw new RuntimeException("Redis setMap error: " + e.getMessage(), e);
        }
    }

    public static <K, V> void setMap(String key, Map<K, V> map, long timeout, TimeUnit unit) {
        try {
            String json = objectMapper.writeValueAsString(map);
            stringRedisTemplate.opsForValue().set(key, json, timeout, unit);
        } catch (Exception e) {
            throw new RuntimeException("Redis setMap with timeout error: " + e.getMessage(), e);
        }
    }

    public static <K, V> Map<K, V> getMap(String key, TypeReference<Map<K, V>> typeRef) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("Redis getMap error: " + e.getMessage(), e);
        }
    }

    // ==================== 其他辅助方法 ====================

    public static void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public static boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}
