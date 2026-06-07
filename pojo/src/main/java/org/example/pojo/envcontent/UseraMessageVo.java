package org.example.pojo.envcontent;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户聊天消息实体类
 * 既可用于存储单条消息，也可用于会话列表（每条代表一个对话及最新消息）
 */
@Data
public class UseraMessageVo {

    /**
     * 消息ID（主键）
     */
    private Long id;

    /**
     * 发送者用户ID
     */
    private Long senderId;

    /**
     * 接收者用户ID
     */
    private Long receiverId;

    /**
     * 最后一条消息内容
     */
    private String content;


    /**
     * 消息状态：0-已发送, 1-已读, 2-撤回等
     */
    private Integer status;

    /**
     * 创建时间（发送时间）
     */
    private LocalDateTime createTime;

    /**
     * 更新时间（如已读时间）
     */
    private LocalDateTime updateTime;

    /**
     * （仅用于会话列表）对方用户昵称或头像等冗余字段（可选）
     */
    private String otherUserNickname;

    private String otherUserAvatar;

    /**
     * （仅用于会话列表）未读消息数量（可选）
     */
    private Integer unreadCount;
}