package org.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.collectionShop;

import java.util.List;

@Mapper
public interface collectionShopMapper extends BaseMapper<collectionShop> {
    List<collectionShop> selectByUserId(Long userId);

    Boolean existByShopIdAndUserId(Long shopId, Long userId);

    Boolean deleteByShopIdAndUserId(Long shopId, Long userId);

    Boolean insert(Long userId, Long shopId);
}
