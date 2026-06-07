package org.example.server.sever.serviceImpl;

import aj.org.objectweb.asm.Handle;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.common.utils.AliyunOssUtils;
import org.example.common.utils.BaiduMapUtils;
import org.example.common.utils.UserHolder;
import org.example.pojo.dto.ContactInfoDTO;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.dto.UserInfoDTO;
import org.example.pojo.entity.UserMessageSession;
import org.example.pojo.envcontent.UserMessageListVo;
import org.example.pojo.envcontent.userDetailedInformation;
import org.example.server.mapper.UserMessageMapper;
import org.example.server.mapper.UserMessageSessionMapper;
import org.example.server.mapper.userDetailedInformationDataMapper;
import org.example.server.sever.ShopService;
import org.example.server.sever.UserDetailedInformationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailedInformationServiceImpl extends ServiceImpl<userDetailedInformationDataMapper, userDetailedInformation> implements UserDetailedInformationService {
    private final AliyunOssUtils aliyunOssUtils;
    private final BaiduMapUtils baiduMapUtils;
    private final UserMessageServiceImpl userMessageService;
    private final UserMessageMapper userMessageMapper;
    private final UserMessageSessionMapper userMessageSessionMapper;

    @Override
    public Result getUserInfo(String youxiang){

        userDetailedInformation userDetailedInformation = query()
                .eq("youxiang", youxiang) // 直接写数据库字段名
                .one();
        return Result.success(userDetailedInformation);
    }
    @Override
    public Result updateUserInfo(UserInfoDTO userInfoDTO){
        double[] lngLat = baiduMapUtils.getPreciseLngLat(userInfoDTO.getAddress());
        userDetailedInformation userDetailedInformation = new userDetailedInformation();
        if(userInfoDTO.getAvatar() != null){
            String avatarUrl = aliyunOssUtils.uploadImage(userInfoDTO.getAvatar());
            System.out.println(avatarUrl);
            userDetailedInformation.setAvatar(avatarUrl);
        }
        userDetailedInformation.setUsername(userInfoDTO.getUsername());
        userDetailedInformation.setGender(userInfoDTO.getGender());
        userDetailedInformation.setLongitude(lngLat[0]);
        userDetailedInformation.setLatitude(lngLat[1]);
        userDetailedInformation.setAddress(userInfoDTO.getAddress());
        userDetailedInformation.setId(userInfoDTO.getId());
        userDetailedInformation.setRegion(userInfoDTO.getRegion());
        userDetailedInformation.setRegions(userInfoDTO.getRegions());
        int update = baseMapper.updateUserDetailedInfoById(userDetailedInformation);
        if(update > 0){
            //更新会话列表
            if (UserHolder.getUser().getId()!= null) {
                Integer userId =  UserHolder.getUser().getId();
                List<ContactInfoDTO> contactInfoDTOList = userMessageSessionMapper.selectContactInfo(Long.valueOf(userId));
                for(ContactInfoDTO contactInfoDTO : contactInfoDTOList){
                    userMessageService.addOrUpdateSession(contactInfoDTO);
                }
            }
            return Result.success("更新用户信息成功");
        }else{
            System.out.println("update fail");
            return Result.success("更新用户信息失败");
        }


    }
    @Override
    public boolean insertUserDetailedInfo(userDetailedInformation userDetailedInformation){
        if(userDetailedInformation.getLongitude() == null || userDetailedInformation.getLatitude() == null){
            userDetailedInformation.setLongitude(0.0);
            userDetailedInformation.setLatitude(0.0);
        }
        int i = baseMapper.insertUserDetailedInfo(userDetailedInformation);
        if(i > 0){
            return true;
        }else{
            return false;
        }

    }
    @Override
    public userDetailedInformation selectUserDetailedInfoByAccountId(Integer accountId){
        return baseMapper.selectUserInfoWithDistance(accountId);

    }

//    @Override
//    public Result deleteShop(Long shopId) {
//        UserDTO user = UserHolder.getUser();
//        int userId = user.getId();
//        QueryWrapper<Shop> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("shop_id", shopId) // 注意：这里是数据库字段名/实体类属性名（驼峰转下划线）
//                .eq("user_id", userId);
//
//        int deleteCount = shopService.getBaseMapper ().delete(queryWrapper);
//        return deleteCount > 0 ? Result.success("删除成功") : Result.error("删除失败");
//    }


}
