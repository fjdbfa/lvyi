package org.example.server.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.common.result.Result;
import org.example.common.utils.UserHolder;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.envcontent.ShopVo;
import org.example.pojo.envcontent.shopId;
import org.example.server.sever.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商店控制器类
 * 处理与商店相关的HTTP请求
 */
@RestController
@RequestMapping("/shop")
public class ShopController {
    /**
     * 自动注入的ShopService实例
     * 用于处理商店相关的业务逻辑
     */
    @Autowired
    private ShopService shopService;
    /**
     * 插入商店信息的接口
     * @param shop 商店数据传输对象，包含要插入的商店信息
     * @return 返回操作结果，包含插入操作的状态和数据
     */
    @RequestMapping("/insert")
    public Result insertShop(ShopVo shop) {
        return Result.success(shopService.insertShop(shop));
    }
    @RequestMapping("/myShop")
    public Result selectByIdW() {

        return Result.success(shopService.selectShopById() );
    }
    @RequestMapping(value = "/selectNearbyLocations",method = RequestMethod.GET)
    public Result selectShopByIdWithLngLat() {

        return shopService.selectNearbyLocations();
    }
    @PostMapping("/mine/favorite/change")
    public Result favoriteChange(@RequestBody shopId id) {
        Long shopId1 = id.getShopId();
        return shopService.favoriteChange(shopId1);
    }
    @GetMapping("/collectionShop")
    public Result selectShopById() {
        return shopService.getCollectionShop();
    }
    @PostMapping("/deleteShop")
    public Result deleteShop(@RequestBody shopId id) {
        Long shopId = id.getShopId();
        return shopService.deleteShop(shopId);
    }
    @RequestMapping("/updateShop" )
    public Result updateShop( ShopVo shop) {
        return shopService.updateShop(shop);
    }


}
