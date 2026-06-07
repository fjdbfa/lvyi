package org.example.server.sever;

import org.example.common.result.Result;
import org.example.pojo.dto.ContactInfoDTO;
import org.example.pojo.envcontent.UserMessageHistoryVo;
import org.example.pojo.envcontent.UserMessageListVo;

import java.util.List;

/**
 * 用户消息服务接口
 * 该接口定义了用户消息相关的业务方法，包括获取会话列表、聊天历史和添加或更新会话信息等功能
 */
public interface UserMessageService {
    /**
     * 获取用户的会话列表
     * @param userId 用户ID
     * @return 返回用户会话列表，包含会话的基本信息
     */
    List<UserMessageListVo> getConversationList(Long userId);
    /**
     * 获取两个用户之间的聊天历史记录
     * @param currentUserId 当前用户ID
     * @param otherUserId 其他用户ID
     * @return 返回两个用户之间的聊天历史记录列表
     */
    List<UserMessageHistoryVo> getChatHistory(Long currentUserId, Long otherUserId);
    // 发送消息方法（已被注释）
    // /**
    //  * 发送消息给指定用户
    //  * @param senderId 发送者ID
    //  * @param receiverId 接收者ID
    //  * @param content 消息内容
    //  * @return 返回操作结果
    //  */
//    Result sendMessage( Long senderId ,  Long receiverId
//            ,  String content);

    /**
     * 添加或更新会话信息
     * @param contactInfo 联系人信息DTO，包含会话相关数据
     * @return 返回操作结果
     */
    Result addOrUpdateSession(ContactInfoDTO contactInfo);
}
