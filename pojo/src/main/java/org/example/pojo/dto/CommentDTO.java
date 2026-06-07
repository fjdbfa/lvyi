package org.example.pojo.dto;

import lombok.Data;

/**
 * 评论数据传输对象（DTO）
 * 用于传输评论相关的数据信息
 */
@Data  // 使用Lombok的@Data注解，自动生成getter、setter、toString等方法
public class CommentDTO {
    private Long id;              // 评论ID，唯一标识一条评论
    private String userHeadImg;   // 用户头像URL，用于显示评论者的头像
    private String userName;      // 用户名称，显示评论者的昵称
    private String content;       // 评论内容，用户发表的评论文本
    private String createTime;    // 创建时间，评论发表的日期时间
    private String address;       // 地址信息，评论发表时的地理位置信息
}
