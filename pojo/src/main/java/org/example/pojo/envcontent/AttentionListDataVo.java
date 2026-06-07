package org.example.pojo.envcontent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 关注列表数据值对象(Value Object)
 * 用于封装关注列表相关的数据信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttentionListDataVo {
    // 主键ID，唯一标识一条关注记录
    private Long id;
    // 用户ID，标识关注者或被关注者的用户ID
    private Long userId;
    // 名称，可能是用户昵称或其他标识名称
    private String name;
    // 头像URL，用户头像的链接地址
    private String avatar;
    // 是否已关注的标志，true表示已关注，false表示未关注
   private Boolean isFollow;
}