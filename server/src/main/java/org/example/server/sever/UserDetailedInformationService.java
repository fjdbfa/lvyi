package org.example.server.sever;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.result.Result;
import org.example.pojo.dto.UserInfoDTO;
import org.example.pojo.envcontent.userDetailedInformation;

public interface UserDetailedInformationService extends IService<userDetailedInformation> {
    Result getUserInfo(String youxiang);

    Result updateUserInfo(UserInfoDTO userInfoDTO);

    boolean insertUserDetailedInfo(userDetailedInformation userDetailedInformation);

    userDetailedInformation selectUserDetailedInfoByAccountId(Integer accountId);


//    Result deleteShop(Long shopId);


}

