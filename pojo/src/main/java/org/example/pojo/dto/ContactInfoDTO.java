package org.example.pojo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 联系人信息 DTO (Data Transfer Object)
 * 用于传输联系人相关数据
 */
@Data  // Lombok注解，自动生成getter、setter、toString等方法
@NoArgsConstructor  // Lombok注解，自动生成无参构造方法
@AllArgsConstructor // Lombok注解，自动生成包含所有字段的有参构造方法
public class ContactInfoDTO {
    private Long senderId;  // 发送者ID，标识消息发送方
    private Long contactId; // 联系人ID，标识与发送者相关的联系人

}