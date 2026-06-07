package org.example.pojo.envcontent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserMessageVo {
    private Long senderId;      // 发送者ID
    private Long receiverId;    // 接收者ID
    private String content;
}
