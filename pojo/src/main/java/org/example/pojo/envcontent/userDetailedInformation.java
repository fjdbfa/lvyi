package org.example.pojo.envcontent;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户详细信息实体类
 * 使用MyBatis-Plus注解进行数据库映射
 * autoResultMap = true 表示自动构建结果映射
 */
@Data
@TableName(value = "user_detailed_information", autoResultMap = true)
public class userDetailedInformation {
    private Integer id; // 用户ID
    private String username; // 用户名
    private String gender; // 性别
    private String avatar;//头像
    private String address;//地址
    // 经纬度POINT字段（数据库类型），查询时需手动转换为经纬度
    @TableField("coord")
    private String coord; // 存储数据库返回的POINT字符串（如"POINT(116.3 39.9)"）

    // 临时字段：用于接收解析后的经度（非数据库字段）
    @TableField(exist = false)
    private Double longitude;

    // 临时字段：用于接收解析后的纬度（非数据库字段）
    @TableField(exist = false)
    private Double latitude;
    private String youxiang;//邮箱
    private Integer accountId;//账号id
    private String regions;//区域详细
    private String region;//区域

}
