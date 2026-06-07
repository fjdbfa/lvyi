package org.example.server.sever.serviceImpl;

import cn.hutool.core.bean.BeanUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.common.utils.UserHolder;
import org.example.pojo.dto.ContactInfoDTO;
import org.example.pojo.entity.UserMessageSession;
import org.example.pojo.envcontent.UserMessageHistoryVo;
import org.example.pojo.envcontent.UserMessageListVo;
import org.example.pojo.envcontent.userDetailedInformation;
import org.example.server.handler.ChatWebSocketHandler;
import org.example.server.mapper.FollowMapper;
import org.example.server.mapper.UserMessageSessionMapper;
import org.example.server.mapper.UserMessageMapper;
import org.example.server.mapper.userDetailedInformationDataMapper;
import org.example.server.sever.UserMessageService;
import org.example.server.sever.UserService;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.example.common.constant.RedisConstants.CONVERSATION_CACHE_KEY_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageServiceImpl implements UserMessageService {



    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
// ← 新增
    private  final UserMessageSessionMapper sessionMapper;
    private  final FollowMapper followMapper;
    private static final long CONVERSATION_CACHE_TTL_MINUTES = 5;
  private  final userDetailedInformationDataMapper  userService;

/**
 * 获取用户对话列表的方法
 * 首先尝试从Redis缓存中获取数据，如果缓存不存在则从数据库查询，并将结果缓存到Redis
 *
 * @param userId 用户ID
 * @return 用户对话列表
 */
//    public List<UserMessageListVo> getConversationList(Long userId) {
//    // 注释掉的日志记录代码
//        log.info("开始获取用户对话列表数据 用户id:{}",userId);
//    // 构建缓存键，使用用户ID作为后缀
//        String cacheKey = CONVERSATION_CACHE_KEY_PREFIX + userId;  // 构建缓存键，格式为"前缀+用户ID"
//    // 尝试从Redis缓存中获取数据
//        Object cached = redisTemplate.opsForValue().get(cacheKey);
//        log.info("从Redis缓存中获取用户对话列表数据{}",cached);
//        if (cached != null) {
//            try {
//            // 如果缓存存在，尝试将缓存数据转换为List<UseraMessageVo>对象并返回
//                return objectMapper.readValue(cached.toString(), new TypeReference<List<UserMessageListVo>>() {});
//            } catch (Exception ignored) {
//                log.info("从Redis缓存中获取用户对话列表数据失败");
//            }
//        }
//
//        List<UserMessageSession> conversations = sessionMapper.selectByUserId(userId);  // 从数据库查询用户会话列表
//        List<UserMessageListVo> result = conversations.stream()  // 使用Stream流处理转换数据
//                .map(session -> {
//                    return BeanUtil.copyProperties(session, UserMessageListVo.class);  // 复制属性到目标对象
//                })
//                .toList();
//        result.forEach(vo -> {
//            boolean exists = followMapper.existsByFollowerAndFollowee(userId, vo.getContactId());
//            vo.setIsFollowing( exists);
//        });
//        List<Long> followingIds = followMapper.selectFolloweeIdsByFollower(userId); // 返回 List<Long>
//        Set<Long> followingSet = new HashSet<>(followingIds);
//        result.forEach(vo -> vo.setIsFollowing(followingSet.contains(vo.getContactId())));
//        try {
//            String json = objectMapper.writeValueAsString(result);
//            long ttl = CONVERSATION_CACHE_TTL_MINUTES * 60 + ThreadLocalRandom.current().nextInt(30);
//            redisTemplate.opsForValue().set(cacheKey, json, Duration.ofSeconds(ttl));
//        } catch (Exception ignored) {
//            log.info("缓存用户对话列表数据失败");
//            log.warn("从Redis缓存反序列化失败，userId: {}, error: {}", userId, ignored.getMessage(), ignored);
//        }
//        log.info("获取用户对话列表数据成功{}", result);
//    if(result.isEmpty()){
//        log.info("用户没有对话");
//        return new ArrayList<>();
//    }
//        return result ;
//    }
    public List<UserMessageListVo> getConversationList(Long userId) {
        if (userId == null || userId <= 0) {
            return Collections.emptyList();
        }

        String cacheKey = CONVERSATION_CACHE_KEY_PREFIX + userId;

        // 尝试从缓存读取
        try {
            String cached = (String) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<List<UserMessageListVo>>() {});
            }
        } catch (IOException e) {
            log.warn("缓存反序列化失败，userId: {}, error: {}", userId, e.getMessage(), e);
            redisTemplate.delete(cacheKey); // 清除脏数据
        }

        // 查询数据库
        List<UserMessageSession> conversations = sessionMapper.selectByUserId(userId);
        List<UserMessageListVo> result = conversations.stream()
                .map(session -> BeanUtil.copyProperties(session, UserMessageListVo.class))
                .toList();

        // 填充关注状态
        List<Long> followingIds = followMapper.selectFolloweeIdsByFollower(userId);
        Set<Long> followingSet = new HashSet<>(followingIds);
        result.forEach(vo -> vo.setIsFollowing(followingSet.contains(vo.getContactId())));

        // 写入缓存
        try {
            String json = objectMapper.writeValueAsString(result);
            long ttl = CONVERSATION_CACHE_TTL_MINUTES * 60L + ThreadLocalRandom.current().nextInt(30);
            redisTemplate.opsForValue().set(cacheKey, json, Duration.ofSeconds(ttl));
        } catch (Exception e) {
            log.error("缓存写入失败，userId: {}", userId, e);
        }

        log.info("获取用户对话列表成功, userId: {}, size: {}", userId, result.size());
        return result;
    }

/**
 * 获取两个用户之间的聊天历史记录
 *
 * @param currentUserId 当前登录用户的ID
 * @param otherUserId   聊天对象的用户ID
 * @return 返回两个用户之间的聊天历史记录列表，包含消息内容、发送时间等信息
 */
    public List<UserMessageHistoryVo> getChatHistory(Long currentUserId, Long otherUserId) {
        return sessionMapper.selectMessagesBetweenUsers(currentUserId, otherUserId);
    }


//    @Override
//    public Result addOrUpdateSession(ContactInfoDTO contactInfo) {
//
//      log.info("开始更新对话列表{}",contactInfo);
//        UserMessageSession session = sessionMapper.selectOne(contactInfo.getSenderId(), contactInfo.getContactId());
//
//        LocalDateTime now = LocalDateTime.now();
//        if (session == null) {
//            userDetailedInformation sender = userService.getuserData(contactInfo.getSenderId());
//            userDetailedInformation receiver = userService.getuserData(contactInfo.getContactId());
//            session = new UserMessageSession();
//            session.setUserId(Long.valueOf(sender.getAccountId()));
//            session.setUserName(sender.getUsername());
//            session.setUserAvatar(sender.getAvatar());
//            session.setContactId(Long.valueOf(receiver.getAccountId()));
//            session.setContactName(receiver.getUsername());
//            session.setContactAvatar(receiver.getAvatar());
//            session.setUpdatedAt(now);
//            sessionMapper.insert(session);
//        }
//        return Result.success();
//    }
@Override
public Result addOrUpdateSession(ContactInfoDTO contactInfo) {
    // 参数校验
    if (contactInfo == null
            || contactInfo.getSenderId() == null
            || contactInfo.getContactId() == null) {
        log.warn("会话参数无效: {}", contactInfo);
        return Result.error("参数错误");
    }
    Long senderId = contactInfo.getSenderId();
    Long contactId = contactInfo.getContactId();
    // 不能和自己建会话
    if (senderId.equals(contactId)) {
        log.warn("用户尝试与自己建立会话, userId={}", senderId);
        return Result.error("不能与自己建立会话");
    }
    log.info("开始更新对话列表, senderId={}, contactId={}", senderId, contactId);
    try {
        // 构造 session（不再依赖 select 决定 insert/update）
        UserMessageSession session = buildSession(contactInfo, null);
        if (session == null) {
            log.warn("用户不存在, senderId={}, contactId={}", senderId, contactId);
            return Result.error("用户不存在");
        }
        LocalDateTime now = LocalDateTime.now();
        if (session.getCreatedAt() == null) session.setCreatedAt(now);
        session.setUpdatedAt(now);

        // 原子 upsert（依赖唯一索引 uq_user_contact）
        sessionMapper.upsert(session);

        // ✅ 关键：清除本用户的会话列表缓存
        String senderCacheKey = CONVERSATION_CACHE_KEY_PREFIX + senderId;
        redisTemplate.delete(senderCacheKey);
        log.info("会话操作完成并清除缓存, sender={}, contact={}", senderId, contactId);
        return Result.success();

    } catch (Exception e) {
        log.error("会话操作失败, contactInfo={}", contactInfo, e);
        return Result.error("操作失败，请稍后重试");
    }
}
   private UserMessageSession buildSession(ContactInfoDTO contactInfo,UserMessageSession existingSession){
       Long senderId = contactInfo.getSenderId();
       Long contactId = contactInfo.getContactId();
       userDetailedInformation sender = userService.getuserData(senderId);
       userDetailedInformation receiver = userService.getuserData(contactId);

       if (sender == null || receiver == null) {
           log.warn("用户不存在, senderId={}, contactId={}", senderId, contactId);
           return null;
       }
       UserMessageSession session = existingSession != null ? existingSession : new UserMessageSession();
// 仅在新建时设置 createdAt（假设类有该字段）
       if (existingSession == null) {
           session.setCreatedAt(LocalDateTime.now());
       }
       session = new UserMessageSession();
       session.setUserId(parseLong(String.valueOf(sender.getAccountId()), senderId));
       session.setUserName(sender.getUsername());
       session.setUserAvatar(sender.getAvatar());
       session.setContactId(parseLong(String.valueOf(receiver.getAccountId()), contactId));
       session.setContactName(receiver.getUsername());
       session.setContactAvatar(receiver.getAvatar());
       session.setUpdatedAt(LocalDateTime.now());
       return session;
    }





    // 辅助方法：安全地将 accountId 转为 Long
    private Long parseLong(String accountIdStr, Long fallback) {
        if (accountIdStr == null || accountIdStr.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Long.parseLong(accountIdStr);
        } catch (NumberFormatException e) {
            log.warn("无效的 accountId: {}, 使用 fallback: {}", accountIdStr, fallback);
            return fallback;
        }
    }
}