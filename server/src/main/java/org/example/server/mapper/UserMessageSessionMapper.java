// src/main/java/com/example/demo/mapper/UserMessageSessionMapper.java
package org.example.server.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.pojo.dto.ContactInfoDTO;
import org.example.pojo.entity.UserMessageSession;
import org.example.pojo.envcontent.UserMessageHistoryVo;

import java.util.List;

@Mapper
public interface UserMessageSessionMapper extends BaseMapper<UserMessageSession> {

    /**
     * 查询某用户的全部聊天会话（按置顶 + 最后消息时间排序）
     */
    List<UserMessageSession> selectByUserId(Long userId);
    List<UserMessageSession> selectByContactId(Long contactId);
    /**
     * 查询用户与某个联系人的会话（用于更新最后消息）
     */
    List<UserMessageHistoryVo>  selectMessagesBetweenUsers(@Param("userId") Long userId,
                                                           @Param("contactId") Long contactId);
    int insert(UserMessageSession  session);
    /**
     * 插入新的聊天会话（首次聊天时创建）
     */
    void upsert(UserMessageSession  session);

    List<ContactInfoDTO> selectContactInfo(@Param("userId") Long userId);

    UserMessageSession selectOne(Long userId, Long contactId);


    void updateContact(UserMessageSession session);
    void updateUser(UserMessageSession session);
}