package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor

@TableName(value = "follows", autoResultMap = true)
public class Follow {
    @TableId(type = IdType.AUTO)
    private Long id;                 // 主键ID
    @TableField("follower_id")
    private Long followerId;         // 关注者ID（谁在关注）
    @TableField("followee_id")
    private Long followeeId;         // 被关注者ID（被谁关注）
    @TableField("created_at")
    private LocalDateTime createdAt; // 创建时间


    public Follow(Long followerId, Long followeeId, LocalDateTime createdAt) {
        this.followerId = followerId;
        this.followeeId = followeeId;
        this.createdAt = createdAt;
    }
}