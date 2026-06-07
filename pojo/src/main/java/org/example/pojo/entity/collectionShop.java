package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "collection_shop", autoResultMap = true)
public class collectionShop {
    private int id;
    private int userId;
    private int shopId;
}
