package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Emp实体类
 * 使用@Data注解来自动生成getter、setter、toString等方法
 * 使用@TableName注解来指定对应的数据库表名为"emp"
 * 设置autoResultMap = true表示自动开启结果集映射
 */
@Data
@TableName(value = "emp", autoResultMap = true)
public class Emp {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String password;
    private String youxiang;
}
