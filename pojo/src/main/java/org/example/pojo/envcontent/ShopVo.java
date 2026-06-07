package org.example.pojo.envcontent;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ShopVo {
    private Integer id;
    private List<MultipartFile> images;
    private String shopDetails;//商品描述

    private String address; // 原始地址

    private Double price;//商品价格

    private Double referencePrice;//商品参考价格

    private Long userId;//用户ID

    private String imageUrls;//在更改的时候需要传入，在插入的时候不需要




}
