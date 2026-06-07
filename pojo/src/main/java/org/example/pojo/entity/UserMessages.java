package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

import java.time.LocalDateTime;

@Data
@TableName("user_messages") // 表名也同步改为 usera_message
public class UserMessages {
    private Long id;            // 消息ID，主键
    private Long senderId;      // 发送者ID
    private Long receiverId;    // 接收者ID
    private String content;     // 消息内容
    private LocalDateTime createdAt;  // 消息创建时间

    // 非数据库字段：用于返回联系人信息
    @TableField(exist = false)
    private SecurityProperties.User contactUser;  // 联系人用户信息，不映射到数据库字段
}