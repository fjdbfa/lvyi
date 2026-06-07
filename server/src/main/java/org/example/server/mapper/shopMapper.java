package org.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.entity.Shop;

import java.util.List;

public interface shopMapper extends BaseMapper<Shop> {
    List<Shop> selectShopById(int id);
    int insertShop(Shop shop);
    List<Shop> selectNearbyLocation(
            @Param("targetLng") double targetLng,  // 经度：注解名=变量名=targetLng
            @Param("targetLat") double targetLat
                // 预计算的上边界纬度
    );
//    @Param("minX") double minX,    // 预计算的左边界经度
//    @Param("minY") double minY,    // 预计算的下边界纬度
//    @Param("maxX") double maxX,    // 预计算的右边界经度
//    @Param("maxY") double maxY
}
