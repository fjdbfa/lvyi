package org.example.pojo.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName(value = "post_comment", autoResultMap = true)
public class PostComment {

    private Long id;

    private Long postId;

    private Long userId;

    private String userName;

    private String userHeadImg;

    private String content;

    private LocalDateTime createTime;


    private Integer status; // TINYINT 映射为 Integer
    private String address;
}
