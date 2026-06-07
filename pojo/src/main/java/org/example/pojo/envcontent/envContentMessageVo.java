package org.example.pojo.envcontent;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
public class envContentMessageVo {
    //文章id
    private Integer id;
    //文章类型
    private String type;
}
