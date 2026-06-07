package org.example.pojo.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 提交创意中心数据传输对象
 * 用于封装创意中心提交相关的数据信息
 */
@Data
public class SubmitCreativeCenterDataDto {
    private Integer userId;    // 用户ID，标识提交创意的用户
    private String type;      // 创意类型，用于区分不同类别的创意
    private String title;     // 创意标题，创意的简短名称或主题
    private String detail;    // 创意详情，包含创意的具体描述信息
    private String source;    // 创意来源，记录创意的出处或灵感来源
    private MultipartFile image;  // 创意相关图片文件，用于可视化展示创意内容
}
