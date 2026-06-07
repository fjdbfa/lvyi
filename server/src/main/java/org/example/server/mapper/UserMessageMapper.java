package org.example.server.mapper;
import org.apache.ibatis.annotations.*;
import org.example.pojo.dto.ContactInfoDTO;
import org.example.pojo.entity.UserMessages;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserMessageMapper 接口
 * 这是一个数据访问对象(DAO)接口，用于处理用户消息相关的数据库操作
 * 使用 MyBatis 的注解方式来映射 SQL 语句
 */
@Mapper  // MyBatis 注解，表示这是一个 Mapper 接口
public interface UserMessageMapper {

    /**
     * 插入一条新的用户消息
     * @param message 包含消息信息的 UserMessages 对象
     */
    @Insert("INSERT INTO user_messages(sender_id, receiver_id,content, created_at) " +
            "VALUES(#{senderId}, #{receiverId}, #{content}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")  // 配置主键自增
    void insert(UserMessages message);

    /**
     * 根据消息ID查询消息
     * @param id 消息的唯一标识符
     * @return 返回对应的 UserMessages 对象
     */
    @Select("SELECT * FROM user_messages WHERE id = #{id}")
    UserMessages selectById(Long id);



    /**
     * 查询两个用户之间的所有消息
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @return 返回两个用户之间的消息列表，按创建时间升序排列
     */
    @Select("SELECT * FROM user_messages WHERE sender_id = #{senderId} AND receiver_id = #{receiverId} " +
            "ORDER BY created_at ASC")
    List<UserMessages> selectBetween(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    /**
     * 查询与指定用户相关的所有消息
     * @param userId 用户ID，可以是发送者或接收者
     * @param limit 返回的消息数量限制
     * @return 返回与用户相关的消息列表，按创建时间降序排列
     */
    @Select("SELECT * FROM user_messages WHERE (sender_id = #{userId} OR receiver_id = #{userId}) " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    List<UserMessages> selectAllByUser(@Param("userId") Long userId, @Param("limit") int limit);

    // 可选：标记消息为已读（如果你加了 read_at 字段）
    // @Update("UPDATE user_messages SET read_at = NOW() WHERE id = #{id} AND receiver_id = #{userId}")
    // void markAsRead(@Param("id") Long id, @Param("userId") Long userId);
}