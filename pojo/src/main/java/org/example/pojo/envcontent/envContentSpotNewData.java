package org.example.pojo.envcontent;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "material", autoResultMap = true)
public class envContentSpotNewData {
    List<envContentMinImgData> envContentMinImgData ;
    envContentMaxImgData envContentMaxImgData;

}
