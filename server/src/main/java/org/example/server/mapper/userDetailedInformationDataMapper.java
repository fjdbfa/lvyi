package org.example.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.pojo.envcontent.userDetailedInformation;

public interface  userDetailedInformationDataMapper extends BaseMapper<userDetailedInformation> {
        int insertUserDetailedInfo(userDetailedInformation userDetailedInformation);
        int updateUserDetailedInfoById(userDetailedInformation userDetailedInformation);
        userDetailedInformation selectUserInfoWithDistance(Integer accountId);

         userDetailedInformation getuserData(Long accountId);

}
