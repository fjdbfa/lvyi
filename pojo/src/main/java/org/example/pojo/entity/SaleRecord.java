// src/main/java/com/example/secondhandvaluator/model/SaleRecord.java
package org.example.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleRecord {
    private String id;
    private String category;   // 如 "智能手机"
    private String brand;      // 如 "Apple"
    private String model;      // 如 "iPhone 13"
    private String condition;  // 如 "9成新"
    private Double price;      // 成交价
    private String description; // 商品描述（用于向量化）


}