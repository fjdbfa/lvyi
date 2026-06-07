package org.example.server.sever.serviceImpl;

import org.example.pojo.entity.collectionShop;
import org.example.server.mapper.collectionShopMapper;
import org.example.server.sever.collectionShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class collectionShopServiceImpl implements collectionShopService {
    @Autowired
    private collectionShopMapper collectionShopMapper;
    @Override
    public List<collectionShop> selectByUserId(Long id) {
       return collectionShopMapper.selectByUserId(id);
    }
    @Override
    public Boolean existByShopIdAndUserId(Long shopId, Long userId) {
        return collectionShopMapper.existByShopIdAndUserId(shopId, userId);
    }

    @Override
    public Boolean deleteByShopIdAndUserId(Long shopId, Long id1) {
        return collectionShopMapper.deleteByShopIdAndUserId(shopId, id1);

    }

    @Override
    public Boolean insert(Long id1, Long shopId) {
        return collectionShopMapper.insert(id1, shopId);
    }

//    @Override
//    public List<collectionShop> selectByUserId(int userId) {
//        return collectionShopMapper.selectByUserId(userId);
//    }

}
