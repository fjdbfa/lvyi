package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;


/**
 * 精选论坛实体类
 * 使用了Lombok的@Data和@ToString注解，自动生成getter、setter、equals、hashCode和toString方法
 * @TableName注解指定了对应的数据库表名为"forum_choiceness"，并开启了自动结果映射
 */
@Data
@ToString
@TableName(value = "forum_choiceness", autoResultMap = true)
public class ForumChoiceness {
    // 主键ID，用于唯一标识每条记录
    private Long id;
    // 用户ID，关联到用户表的外键
    private Long userId;
    // 内容类型，用于区分不同类型的论坛内容
    private String type;
    // 标题，论坛内容的标题
    private String title;
    // 详情内容，论坛内容的详细描述
    private String detail;
    // 内容来源，标识内容的来源渠道
    private String source;
    // 参与人数，默认值为0，记录参与该论坛内容的人数
    private Integer numberPeople = 0;
    // 图片路径，关联到论坛内容的图片
    private String img;
    // 创建时间，记录内容创建的时间
    private LocalDateTime createdAt;
    // 导航类型，用于分类和导航
    private String navType;
    // 更新时间，记录内容最后更新的时间
    private LocalDateTime updatedAt;
    /**
     * 创建时的回调方法
     * 在实体对象创建时自动调用，设置创建时间和更新时间为当前时间
     */
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    /**
     * 更新前的回调方法
     * 在实体对象更新前自动调用，仅更新更新时间为当前时间
     * 使用@TableField注解标记，由MyBatis-Plus在更新操作前自动执行
     */
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
