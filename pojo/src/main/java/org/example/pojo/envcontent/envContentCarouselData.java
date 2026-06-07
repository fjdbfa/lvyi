package org.example.pojo.envcontent;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 轮播图数据实体类
 * 用于存储轮播图相关的信息
 * 使用@Data注解自动生成getter/setter等方法
 * 使用@TableName注解指定对应的数据库表名为env_content_carousel
 * autoResultMap = true表示自动生成结果映射
 */
@Data
@TableName(value = "env_content_carousel", autoResultMap = true)
public class envContentCarouselData {
    // 轮播图ID，唯一标识
    private Integer id;
    // 轮播图图片路径或URL
    private String img;
    // 轮播图标题
    private String title;
    // 轮播图日期，使用LocalDate类型存储日期
    private LocalDate date;
    // 轮播图地址或相关信息
    private String address;
}
