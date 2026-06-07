package org.example.server.sever;

import org.example.common.result.Result;
import org.example.pojo.entity.collectionShop;
import org.springframework.stereotype.Service;

import java.util.List;


public interface collectionShopService {


    List<collectionShop> selectByUserId(Long id);
    Boolean existByShopIdAndUserId(Long shopId, Long userId);

    Boolean deleteByShopIdAndUserId(Long shopId, Long id1);

    Boolean insert(Long id1, Long shopId);

//    List<collectionShop> selectByUserId(int userId);
}
