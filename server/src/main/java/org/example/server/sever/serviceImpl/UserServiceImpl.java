package org.example.server.sever.serviceImpl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.assist.ISqlRunner;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
//import org.example.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.example.common.exception.BusinessException;
import org.example.common.result.Result;


import org.example.common.utils.UserHolder;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.dto.UserRegistDTO;
import org.example.pojo.entity.Emp;
import org.example.pojo.entity.Follow;
import org.example.pojo.entity.ForumChoiceness;
import org.example.pojo.envcontent.AttentionListDataVo;
import org.example.pojo.envcontent.UserDataVo;
import org.example.pojo.envcontent.userDetailedInformation;
import org.example.pojo.user.userLogin;
import org.example.server.mapper.FollowMapper;
import org.example.server.mapper.ForumChoicenessMapper;
import org.example.server.mapper.userDetailedInformationDataMapper;
import org.example.server.mapper.userMapper;
import org.example.server.sever.UserDetailedInformationService;
import org.example.server.sever.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.example.common.constant.RedisConstants.CONVERSATION_CACHE_KEY_PREFIX;
import static org.example.common.constant.RedisConstants.LOGIN_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<userMapper, Emp> implements UserService {


    private final StringRedisTemplate redisTemplate;
      private  final ObjectMapper  objectMapper;
      private  final UserDetailedInformationService  userDetailedInformationService ;
      private final  FollowMapper followMapper;
      private final userDetailedInformationDataMapper userDetailedInformationDataMapper;
      private final ForumChoicenessMapper forumChoicenessMapper;

    public Result<Object>  existsByIdAndRegist(UserRegistDTO Dto) {

//        boolean exists = lambdaQuery()
//                .eq(Emp::getName, Dto.getName())
//                .exists();
        boolean exists1 = lambdaQuery()
                .eq(Emp::getYouxiang, Dto.getYouxiang())
                .exists();
        if(exists1){
            return Result.error("邮箱已被注册，此次注册失败");
        }
//        if(exists){
//            return Result.success("用户名已被注册，此次注册失败");
//        }
          String code=Dto.getCode();
          String o = redisTemplate.opsForValue().get(Dto.getYouxiang());
          boolean m=code.equalsIgnoreCase(o);//不区分大小写


        if(!exists1 && m){
            Emp emp=new Emp();
            BeanUtils.copyProperties(Dto, emp);
            emp.setName("user"+Dto.getYouxiang());
            boolean save = save(emp);
            userDetailedInformation userDetailedInformation = new userDetailedInformation();
            userDetailedInformation.setUsername(emp.getName());
            userDetailedInformation.setYouxiang(emp.getYouxiang());
            userDetailedInformation.setAccountId(emp.getId());
            userDetailedInformationService.insertUserDetailedInfo( userDetailedInformation);
            if(save){
                return Result.success("regist success");
            }

        }

        return Result.error("regist failed");

    }

    @Override
    public String login(userLogin login) throws JsonProcessingException {
        log.info("login");
        Emp emp = lambdaQuery()
                .eq(Emp::getYouxiang, login.getYouxiang())
                .eq(Emp::getPassword, login.getPassword())
                .one();
        if(emp==null){
            throw new BusinessException("用户不存在，登录失败");
        }
//        map.put("id",  String.valueOf(emp.getId()));
//        map.put("username", emp.getName());
//        map.put("youxiang", emp.getYouxiang());
        String userJson = objectMapper.writeValueAsString(
                BeanUtil.copyProperties(emp, UserDTO.class)
        );
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
        redisTemplate.opsForValue().set(LOGIN_TOKEN +uuid,userJson );

        redisTemplate.expire(LOGIN_TOKEN +uuid,30, TimeUnit.MINUTES);
        System.out.println("登录成功");
        log.info(uuid);
        return uuid;


    }
    @Override
    public Result<Object> updatePassword(UserRegistDTO Dto) {


        boolean exists1 = lambdaQuery()
                .eq(Emp::getYouxiang, Dto.getYouxiang())
                .exists();
        if(!exists1){
            return Result.error("此邮箱未绑定用户");
        }

        String code=Dto.getCode();
        String o = redisTemplate.opsForValue().get("updatePassword"+Dto.getYouxiang());
        boolean m=code.equalsIgnoreCase(o);//不区分大小写

        if(exists1 && m){
            LambdaUpdateWrapper<Emp> queryWrapper = new LambdaUpdateWrapper<>();
            queryWrapper.eq(Emp::getYouxiang, Dto.getYouxiang())
                    .set(Emp::getPassword, Dto.getPassword());
            boolean update = update(queryWrapper);

            if(update){
                return Result.success("update success");
            }

        }

        return Result.error("regist failed");

    }

    @Override
    public Result follow(Long userid) {
        Long id = Long.valueOf(UserHolder.getUser().getId());
        log.info(String.valueOf(id));
        boolean exists = followMapper.existsByFollowerAndFollowee(id, userid);
        if(!exists){
            followMapper.insert(new Follow(id, userid, LocalDateTime.now()));
            redisTemplate.delete(CONVERSATION_CACHE_KEY_PREFIX + id);
             return Result.success("关注成功");
        }
        else {
            followMapper.deleteByFollowerAndFollowee(id, userid);
            redisTemplate.delete(CONVERSATION_CACHE_KEY_PREFIX + id);
            return Result.success("取消关注成功");
        }

    }

    @Override
    public Result<List<AttentionListDataVo>> followList(Long userId) {
        log.info(userId.toString());

        List<Follow> follows = followMapper.selectByFollowerId(userId);
        List<AttentionListDataVo> attentionListDataVos = follows.stream()
                .map(follow -> {
                    AttentionListDataVo attentionListDataVo = new AttentionListDataVo();
                    attentionListDataVo.setUserId(follow.getFolloweeId());
                    attentionListDataVo.setName(userDetailedInformationDataMapper.getuserData(follow.getFolloweeId()).getUsername());
                    attentionListDataVo.setAvatar(userDetailedInformationDataMapper.getuserData(follow.getFolloweeId()).getAvatar());
                    boolean exists = followMapper.existsByFollowerAndFollowee(userId, follow.getFolloweeId());
                    attentionListDataVo.setIsFollow(exists);
                    return attentionListDataVo;
                }).toList();

        return Result.success(attentionListDataVos);
    }

    @Override
    public Result<UserDataVo> unfollow(Long userId) {
        Long followCount = followMapper.countByFolloweeId(userId);

        Long greenPoints = forumChoicenessMapper.selectCount(new QueryWrapper<ForumChoiceness>().eq("user_id", userId));
        UserDataVo userDataVo = new UserDataVo(greenPoints, followCount);
        return Result.success(userDataVo);
    }

}
