package org.example.server.sever.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.result.Result;
import org.example.common.utils.AliyunOssUtils;
import org.example.common.utils.BaiduMapUtils;
import org.example.common.utils.UserHolder;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.entity.Shop;
import org.example.pojo.entity.collectionShop;
import org.example.pojo.envcontent.ShopDto;
import org.example.pojo.envcontent.ShopVo;
import org.example.pojo.envcontent.userDetailedInformation;
import org.example.server.mapper.shopMapper;
import org.example.server.sever.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShopServiceImpl extends ServiceImpl<shopMapper, Shop> implements ShopService {
    @Autowired
    private shopMapper shopMapper;
    @Autowired
    private BaiduMapUtils baiduMapUtils;
    @Autowired
    private AliyunOssUtils ossUtils;
    @Autowired
    private UserDetailedInformationServiceImpl userDetailedInformationServiceImpl;
    @Autowired
    private collectionShopServiceImpl collectionShopServiceImpl;


    @Override
    public Result deleteShop(Long shopId) {
        UserDTO user = UserHolder.getUser();
        int userId = user.getId();
        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", shopId) // 注意：这里是数据库字段名/实体类属性名（驼峰转下划线）
                .eq("user_id", userId);

        int deleteCount = shopMapper.delete(queryWrapper);
        return deleteCount > 0 ? Result.success("删除成功") : Result.error("删除失败");
    }

    @Override
    public int insertShop(ShopVo shop) {
        double[] lngLat = baiduMapUtils.getPreciseLngLat(shop.getAddress());
        Shop shop1 = new Shop();
        List<String> imageUrls = new ArrayList<>();
        if(shop.getImages() != null) {
            for (MultipartFile file : shop.getImages()) {
                // 上传图片到OSS
                String imageUrl = ossUtils.uploadImage(file);
                imageUrls.add(imageUrl);
            }
        }
        shop1.setLongitude(lngLat[0]);
        shop1.setLatitude(lngLat[1]);
        String jsonString = JSON.toJSONString(imageUrls);
        shop1.setImageUrl(jsonString);
        shop1.setShopDetails(shop.getShopDetails());
        shop1.setAddress(shop.getAddress());
        shop1.setPrice(shop.getPrice());
        shop1.setReferencePrice(shop.getReferencePrice());
        shop1.setUserId(shop.getUserId());
//        shop1.setCity("赣州市");

        shop1.setCoord("POINT(" + lngLat[0] + " " + lngLat[1] + ")");

        return shopMapper.insertShop(shop1);

    }
    @Override//在修改商家商品时使用
    public final int insertShop(ShopVo shop, List<String> keepImages) {
        double[] lngLat = baiduMapUtils.getPreciseLngLat(shop.getAddress());
        Shop shop1 = new Shop();
        List<String> imageUrls = new ArrayList<>();
        if(shop.getImages() != null) {
            for (MultipartFile file : shop.getImages()) {
                // 上传图片到OSS
                String imageUrl = ossUtils.uploadImage(file);
                imageUrls.add(imageUrl);
            }
        }
        for(String imageUrl : keepImages){
            imageUrls.add(imageUrl);
        }
        shop1.setLongitude(lngLat[0]);
        shop1.setLatitude(lngLat[1]);
        String jsonString = JSON.toJSONString(imageUrls);
        shop1.setImageUrl(jsonString);
        shop1.setShopDetails(shop.getShopDetails());
        shop1.setAddress(shop.getAddress());
        shop1.setPrice(shop.getPrice());
        shop1.setReferencePrice(shop.getReferencePrice());
        shop1.setUserId(shop.getUserId());
//        shop1.setCity("赣州市");

        shop1.setCoord("POINT(" + lngLat[0] + " " + lngLat[1] + ")");

        return shopMapper.insertShop(shop1);

    }


    @Override
    public List<ShopDto> selectShopById () {
        UserDTO userdto = UserHolder.getUser();
        Integer id1 = userdto.getId();//empid
//        userDetailedInformation userDetailedInformation = userDetailedInformationServiceImpl.selectUserDetailedInfoByAccountId(id);
//        Integer id1 = userDetailedInformation.getId();
        List<Shop> shops = shopMapper.selectShopById(id1);
        List<ShopDto> shopDtos = new ArrayList<>();
        for(Shop shop1 : shops) {
            ShopDto shopDto = new ShopDto();
            shopDto.setAddress(shop1.getAddress());
            shopDto.setPrice(shop1.getPrice());
            shopDto.setReferencePrice(shop1.getReferencePrice());
            shopDto.setUserId(shop1.getUserId());
            shopDto.setShopDetails(shop1.getShopDetails());
            shopDto.setImageUrls(JSON.parseArray(shop1.getImageUrl(), String.class));
            shopDto.setId(shop1.getId());
            shopDto.setIsCollected(false);
            shopDtos.add(shopDto);
        }

        return shopDtos;
    }

    @Override
    public Result  selectNearbyLocations() {
        UserDTO userdto = UserHolder.getUser();
        int id = userdto.getId();
        userDetailedInformation userDetailedInformation=new userDetailedInformation();
            userDetailedInformation = userDetailedInformationServiceImpl.selectUserDetailedInfoByAccountId(id);
      if(userDetailedInformation == null){
          System.out.println("请先完善个人信息");
          return Result.error("请先完善个人信息", 400) ;
      }


        // 检查用户详细信息是否存在
        if (userDetailedInformation == null) {
            throw new RuntimeException("用户详细信息不存在，用户ID: " + id);
        }

        List<ShopDto > shopDtos = new ArrayList<>();
        Double targetLng = userDetailedInformation.getLongitude();
        Double targetLat = userDetailedInformation.getLatitude();
//        double minX = targetLng - 0.04;  // 左边界经度
//        double minY = targetLat - 0.04;  // 下边界纬度
//        double maxX = targetLng + 0.04;  // 右边界经度
//        double maxY = targetLat + 0.04;  // 上边界纬度
        List<Shop> shops = shopMapper.selectNearbyLocation(targetLng, targetLat);

        for(Shop shop1 : shops) {
            ShopDto shopDto = new ShopDto();
            shopDto.setAddress(shop1.getAddress());
            shopDto.setPrice(shop1.getPrice());
            shopDto.setReferencePrice(shop1.getReferencePrice());
            shopDto.setUserId(shop1.getUserId());
            shopDto.setShopDetails(shop1.getShopDetails());
            shopDto.setImageUrls(JSON.parseArray(shop1.getImageUrl(), String.class));
            shopDto.setId(shop1.getId());
            shopDto.setIsCollected(false);
            shopDtos.add(shopDto);
        }
        Long id1= (long) id;
        List<collectionShop> collectionShops = collectionShopServiceImpl.selectByUserId(id1);
        for(collectionShop collectionShop1 : collectionShops) {
            for(ShopDto shopDto : shopDtos) {
                if(collectionShop1.getShopId() == shopDto.getId()) {
                    shopDto.setIsCollected(true);
                }
            }
        }

        return Result.success(shopDtos);

    }
    @Override
    public Boolean delteShop(Long id) {
        return shopMapper.deleteById(id) > 0;
    }

    @Override
    public Result favoriteChange(Long shopId) {
        UserDTO userdto = UserHolder.getUser();
        Integer id = userdto.getId();
        Long id1 = (long) id;
        if(collectionShopServiceImpl.existByShopIdAndUserId(shopId,id1) ){
            Boolean b = collectionShopServiceImpl.deleteByShopIdAndUserId(shopId, id1);
            if(b){
                return Result.success("取消收藏成功");
            }else{
                return Result.error("取消收藏失败");
            }
        }else{
            Boolean insert = collectionShopServiceImpl.insert(id1, shopId);
            if(insert){
                return Result.success("收藏成功");
            }else{
                return Result.error("收藏失败");
            }
        }
    }

    @Override
    public Result getCollectionShop() {
        UserDTO userdto = UserHolder.getUser();
        Integer id = userdto.getId();
        Long id1= (long) id;
        List<collectionShop> collectionShops = collectionShopServiceImpl.selectByUserId(id1);
        List<ShopDto> shops = new ArrayList<>();
        for(collectionShop collectionShop1 : collectionShops){
            Shop shop = shopMapper.selectById(collectionShop1.getShopId());
            ShopDto shopDto = new ShopDto();
            shopDto.setAddress(shop.getAddress());
            shopDto.setPrice(shop.getPrice());
            shopDto.setReferencePrice(shop.getReferencePrice());
            shopDto.setUserId(shop.getUserId());
            shopDto.setShopDetails(shop.getShopDetails());
            shopDto.setImageUrls(JSON.parseArray(shop.getImageUrl(), String.class));
            shopDto.setId(shop.getId());
            shopDto.setIsCollected(true);
            shops.add(shopDto);
        }
        return Result.success(shops) ;
    }

    @Override
    public Result updateShop(ShopVo shop) {
        //查找原来的商品
        QueryWrapper <Shop> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", shop.getId());
        Shop shop1 = shopMapper.selectOne(queryWrapper);
        //删除要删除的图片
        //找到不要删除的
        String images = shop.getImageUrls();
        //中间，号隔开切割
        List<String> imageUrls1 = new ArrayList<>();
        String[]  split = images.split(",");
        for(String imageUrl : split){
            imageUrls1.add(imageUrl);
        }
        //获取图片路径
        List<String> imageUrls = JSON.parseArray(shop1.getImageUrl(), String.class);
        int x=0;
        //根据图片路径删除图片
        for(String imageUrl : imageUrls){
            x=0;
            for(String imageUrl1 : imageUrls1){
                if(imageUrl.equals(imageUrl1)){
                    x=1;
                    break;
                }
            }
            if(x==0){
                boolean deleteImage = ossUtils.deleteImage(imageUrl);
                //这里可以加一个删除失败把删除失败的这个路径写到某个文件里
                //到时候批量删除
                //现在懒不想写
            }
        }
        //删完图片就可以吧在数据库的删掉
        shopMapper.deleteById(shop1);
        //插入新的商品,和保留原来要留的图片
        int i = insertShop(shop, imageUrls1);
        if(i > 0){
            return Result.success("修改成功");
        }else{
            return Result.error("修改失败");
        }


    }
}
