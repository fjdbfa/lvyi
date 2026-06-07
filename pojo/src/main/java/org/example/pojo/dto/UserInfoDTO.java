package org.example.pojo.dto;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data // Lombok 注解，自动生成 getter/setter
public class UserInfoDTO {
    private Integer id;
    private MultipartFile avatar; // 接收前端端的 File 类型头像
    private String username;
    private String gender;
    private String region;
    private String regions;
    private String address;
}