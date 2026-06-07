package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "shop", autoResultMap = true)
public class Shop {

    private Long id;

    private String imageUrl;//商品图片（数据库存的是json格式）

    private String shopDetails;//商品描述

    private String address; // 原始地址

    private Double price;//商品价格

    private Double referencePrice;//商品参考价格

    private Long userId;//用户ID



//    private String city;//城市



    // 经纬度POINT字段（数据库类型），查询时需手动转换为经纬度
    @TableField("coord")
    private String coord; // 存储数据库返回的POINT字符串（如"POINT(116.3 39.9)"）

    // 临时字段：用于接收解析后的经度（非数据库字段）
    @TableField(exist = false)
    private Double longitude;

    // 临时字段：用于接收解析后的纬度（非数据库字段）
    @TableField(exist = false)
    private Double latitude;
}
