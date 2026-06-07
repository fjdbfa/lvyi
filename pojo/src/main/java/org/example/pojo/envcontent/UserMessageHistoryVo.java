package org.example.pojo.envcontent;

import lombok.Data;

@Data
public class UserMessageHistoryVo {

    private Long senderId;      // 发送方ID
    private String content;
}
