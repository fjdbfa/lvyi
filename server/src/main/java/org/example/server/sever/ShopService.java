package org.example.server.sever;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.result.Result;
import org.example.pojo.entity.Shop;
import org.example.pojo.envcontent.ShopDto;
import org.example.pojo.envcontent.ShopVo;

import java.util.List;

public interface  ShopService extends IService<Shop> {


    Result deleteShop(Long shopId);

    int insertShop(ShopVo shop);

    int insertShop(ShopVo shop, List<String> keepImages);

    List<ShopDto> selectShopById();
    Result  selectNearbyLocations();

    Boolean delteShop(Long id);

    Result favoriteChange(Long shopId);

    Result  getCollectionShop();

    Result updateShop(ShopVo shop);
}
