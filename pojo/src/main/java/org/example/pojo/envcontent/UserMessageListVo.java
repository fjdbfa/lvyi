package org.example.pojo.envcontent;

import lombok.Data;

@Data
public class UserMessageListVo {
    private Long id;
    private Long contactId;
    private String contactName;
    private String contactAvatar;
    private Boolean isFollowing;
}