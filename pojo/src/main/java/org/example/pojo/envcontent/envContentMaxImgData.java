package org.example.pojo.envcontent;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 环境内容-最大图片数据实体类（对应 env_content_max_img_data 表）
 */
@Data
@TableName(value = "env_content_max_img_data", autoResultMap = true)
public class envContentMaxImgData {
    /**
     * 最大图片数据唯一标识（自增主键）
     */
    private Integer id;

//    /**
//     * 关联的外层分组ID（对应外层数组元素的id）
//     */
//    private Integer groupId;

    /**
     * 类型（如"材料"）
     */
    private String type;

    /**
     * 图片路径（URL或本地路径）
     */
    private String imageUrl;

    /**
     * 标题内容
     */
    private String title;

    /**
     * 日期（格式：YYYY-MM-DD）
     */
    private Date date;

    /**
     * 来源地址（如网站域名）
     */
    private String address;
}