package org.example.server.sever;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.common.result.Result;
import org.example.pojo.dto.UserRegistDTO;
import org.example.pojo.entity.Emp;
import org.example.pojo.envcontent.AttentionListDataVo;
import org.example.pojo.envcontent.UserDataVo;
import org.example.pojo.user.userLogin;

import java.util.List;

public interface UserService extends IService<Emp> {
    Result<Object> existsByIdAndRegist(UserRegistDTO Dto);

    String login(userLogin login) throws JsonProcessingException;

    Result<Object> updatePassword(UserRegistDTO Dto);


    Result follow(Long userid);

    Result<List<AttentionListDataVo>> followList(Long userId);

    Result<UserDataVo> unfollow(Long userId);
}
