package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor// 使用Lombok的@Data注解，自动生成getter、setter、toString等方法
public class UserMessageDTO {
    private Long senderId;
    private Long receiverId;  // 接收者ID，用于标识消息的接收方
    private String content;   // 消息内容，实际的聊天文本信息

}