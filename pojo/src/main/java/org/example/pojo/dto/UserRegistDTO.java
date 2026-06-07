package org.example.pojo.dto;

import lombok.Data;

/**
 * 用户注册数据传输对象(Data Transfer Object)
 * 用于封装用户注册相关的数据信息
 */
@Data  // 使用Lombok注解，自动生成getter、setter、toString等方法
public class UserRegistDTO {
    //private String name;  // 用户名（已注释，表示当前未使用）
    private String password;  // 用户密码
    private String youxiang;  // 用户邮箱（youxiang为拼音，表示"邮箱"）
    private String code;      // 验证码
}
