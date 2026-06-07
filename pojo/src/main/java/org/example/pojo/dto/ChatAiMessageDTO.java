package org.example.pojo.dto;

import lombok.Data;

/**
 * 聊天消息数据传输对象
 * 用于在系统各组件之间传递聊天消息信息
 */
@Data  // 使用Lombok的@Data注解，自动生成getter、setter、toString等方法
public class ChatAiMessageDTO {
    private Long UserId;   // 消息ID，用于唯一标识每条消息

    private String message;   // 消息内容，实际的聊天文本信息
     // 消息时间戳，记录消息发送的时间点
}