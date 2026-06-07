package org.example.pojo.envcontent;

import lombok.Data;

import java.util.List;

@Data
public class ShopDto {
    private Long id;
    private List<String> imageUrls;
    private String shopDetails;
    private String address;
    private Double price;
    private Double referencePrice;
    private Long userId;
    private Boolean isCollected;


}
