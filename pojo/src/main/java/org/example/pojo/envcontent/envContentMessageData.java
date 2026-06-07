package org.example.pojo.envcontent;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "env_content_message", autoResultMap = true)
public class envContentMessageData {
    //文章id
    private Integer id;
    //标题
    private String title;
    //标题图片
    private String imageT;
    //文章文本
    private String message;
    //文章图片
    private String image;
    //文章日期
    private Date date;
    //文章类型
    private String type;

}
