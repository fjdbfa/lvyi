package org.example.pojo.dto;

import lombok.Data;

/**
 * 发送评论数据传输对象(DTO)
 * 用于封装和传输评论相关的数据信息
 */
@Data
public class SendCommentDataDto {
    private String userHeadImg;    // 用户头像URL
    private String userName;       // 用户名称
    private String address;     // 用户地址/位置信息
    private Long userId;     // 用户ID
    private Long postId;     // 帖子ID
    private String content;        // 评论内容
}
