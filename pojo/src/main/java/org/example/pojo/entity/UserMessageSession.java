// src/main/java/com/example/demo/entity/UserMessageSession.java
package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户消息会话实体类
 * 用于存储用户与联系人之间的会话信息
 * 包含会话ID、用户信息、联系人信息以及创建和更新时间
 */
@Data
@TableName("user_message_sessions")
public class UserMessageSession {

    /**
     * 会话ID
     * 使用自增策略生成主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 当前用户ID
     * 标识会话的发起方
     */
    @TableField("user_id")
    private Long userId;           // 当前用户ID
    /**
     * 当前用户名称
     * 用于显示用户昵称
     */
    @TableField("user_name")
    private String userName;
    /**
     * 当前用户头像
     * 存储用户头像的URL或标识
     */
    @TableField("user_avatar")
    private String userAvatar;
    /**
     * 联系人ID
     * 标识会话的接收方
     */
    @TableField("contact_id")
    private Long contactId;        // 联系人ID
    /**
     * 联系人昵称
     * 用于显示联系人的昵称
     */
    @TableField("contact_name")
    private String contactName;    // 联系人昵称
    /**
     * 联系人头像
     * 存储联系人头像的URL或标识
     */
    @TableField("contact_avatar")
    private String contactAvatar;  // 联系人头像
    /**
     * 会话创建时间
     * 记录会话首次创建的时间戳
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
    /**
     * 会话最后更新时间
     * 记录会话最后一次更新的时间戳
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}